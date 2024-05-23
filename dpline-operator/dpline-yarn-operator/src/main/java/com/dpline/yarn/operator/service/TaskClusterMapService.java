package com.dpline.yarn.operator.service;

import com.dpline.common.enums.*;
import com.dpline.common.request.TaskAlertEditRequest;
import com.dpline.common.request.TaskAlertEditResponse;
import com.dpline.common.util.Asserts;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.FlinkSession;
import com.dpline.dao.entity.Job;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.dao.mapper.FlinkSessionMapper;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.operator.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class TaskClusterMapService {

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    ClusterMapper clusterMapper;

    private ConcurrentHashMap<Long,ClusterEntity> clusterEntityMap = new ConcurrentHashMap<>();

    /**
     * 任务操作的缓存
     */
    private final ConcurrentHashMap<String, OperationsEnum> operationMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(TaskClusterMapService.class);


    public void addOperationToCache(String jobId,OperationsEnum operation){
        operationMap.put(jobId,operation);
    }

    public OperationsEnum getOperationFromCache(String jobId){
        return operationMap.get(jobId);
    }

    public void delOperationFromCache(String jobId){
        operationMap.remove(jobId);
    }



    /**
     * add new Task to cache
     */
    public ClusterEntity newTaskAddToCache(Job job) {
        ClusterEntity clusterEntity =
                clusterEntityMap.get((job.getClusterId()));
        if (Asserts.isNull(clusterEntity)) {
            clusterEntity = new ClusterEntity(job.getClusterId());
        }
        long currentTimeMillis = System.currentTimeMillis();

        try {
            ExecStatus execStatus = ExecStatus.of(job.getExecStatus());
            AlertMode alertMode = AlertMode.of(job.getAlertMode());
            logger.info("CurrentTimeMillis:[{}], execStatus:[{}], alertMode:[{}], job:[{}]",currentTimeMillis,execStatus,alertMode,job.toString());
            JobTask jobTask = new JobTask(
                    job.getId(),
                    job.getJobName(),
                    currentTimeMillis,
                    execStatus,
                    alertMode,
                    job.getAlertInstanceId(),
                    job.getRunJobId()
            );
//            clusterFlushEntity = clusterEntity
//                    .builder()
//                    .runModeType(RunModeType.K8S_APPLICATION)
//                    .clusterId(job.getJobName())
//                    .clusterEntityId(job.getClusterId())
//                    .build();
            clusterEntity.addNewJob(job.getApplicationId(),
                    RunModeType.of(job.getRunModeType()),
                    job.getRunJobId(),
                    job.getRestUrl(),
                    jobTask);
            clusterEntityMap.putIfAbsent(job.getClusterId(), clusterEntity);
            logger.info("New task [{}], run mode [{}] in applicationSessionId [{}] has add into cache.",
                    job.getJobName(),
                    job.getRunModeType(),
                    job.getRunJobId()
            );
            return clusterEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * cache session
     * TODO rest url is ?
     */
    public void batchCacheAllRunningSessionInst() {
        List<FlinkSession> flinkSessions = flinkSessionMapper.queryAllOnlineFlinkSession(ClusterType.YARN.getValue());
        flinkSessions.forEach(flinkSession -> {
            Long clusterId = flinkSession.getId();
            ClusterEntity engineEntity = clusterEntityMap.get(clusterId);
            if (Asserts.isNull(engineEntity)) {
                engineEntity = new ClusterEntity(flinkSession.getClusterId());
            }
            // if exist,that`s created when cache task
            String applicationId = flinkSession.getApplicationId();
            if (Asserts.isNotNull(engineEntity.getApplicationEntity(applicationId))) {
                return;
            }
//            (applicationId, RunModeType.YARN_SESSION,"")
            engineEntity.addNewJob(ApplicationEntity.builder()
                    .applicationId(applicationId)
                    .runModeType(RunModeType.YARN_SESSION)
                    .restUrl("")
                    .build());
        });
    }

    /**
     * flush and reFlush to cache
     *
     * @return
     */
    public List<SniffEvent> batchCacheAllRunningTaskInst() {
        logger.info("Fetch running flink task and cache it.");
        List<Job> jobList = jobMapper.selectByJobStatus(
            ExecStatus.runningArray());
        clusterEntityMap.clear();
        // add to cache
        jobList.forEach(new Consumer<Job>() {
            @Override
            public void accept(Job job) {
                RunModeType runModeType = RunModeType.of(job.getRunModeType());
                if(!RunModeType.YARN_APPLICATION.equals(runModeType) && !RunModeType.YARN_SESSION.equals(runModeType)){
                    return;
                }
                newTaskAddToCache(job);
            }
        });
        // add to sniff
        ArrayList<SniffEvent> sniffArrayList = new ArrayList<>();
        clusterEntityMap.forEach(
             // 每个 yarn 集群
            (clusterId,clusterEntity)->{
                // yarn 集群中的每个application或者session
                clusterEntity.getApplicationIdMap().forEach(
                    (applicationId,applicationEntity)->{
                        sniffArrayList.add(new SniffEvent(clusterId,applicationId));
                    }
                );
            }
        );
        return sniffArrayList;
    }

    /**
     *
     * delete jobId
     * @param clusterId
     * @param jobId
     */
    public void delJobTask(Long clusterId, String applicationId, String jobId) {
        ClusterEntity clusterEntity = clusterEntityMap.get(clusterId);
        if (Asserts.isNull(clusterEntity)) {
            return;
        }
        ApplicationEntity applicationEntity = clusterEntity.getApplicationEntity(applicationId);
        if (Asserts.isNotNull(applicationEntity)) {
            applicationEntity.removeJob(jobId);
        }
    }

    /**
     * 删除 application 的缓存
     *
     * @param clusterId
     * @param applicationId
     */
    public void delApplication(Long clusterId, String applicationId) {
        ClusterEntity clusterEntity = clusterEntityMap.get(clusterId);
        if (Asserts.isNull(clusterEntity)) {
            return;
        }
        clusterEntity.removeApplicationEntity(applicationId);
        logger.info("ClusterId:[{}] has been deleted from clusterId IngressPath Map", clusterId);
    }


    public Optional<ApplicationEntity> getApplicationEntity(Long clusterId,String applicationId){
        if (clusterEntityMap.containsKey(clusterId)){
            return Optional.ofNullable(clusterEntityMap.get(clusterId).getApplicationEntity(applicationId));
        }
        return Optional.empty();
    }

    public Optional<ClusterEntity> getClusterEntity(Long clusterId){
        return Optional.ofNullable(clusterEntityMap.get(clusterId));
    }



    /**
     * 从缓存中删除对象
     * 如果是application 模式，直接删除 cluster对象
     * 如果是session 模式，删除 内部任务
     *
     * @param clusterId
     * @param jobId
     */
    public void delTaskInstFromCache(Long clusterId, String applicationId,String jobId) {
        // application mode, delete application
        if (clusterEntityMap.get(clusterId).getApplicationEntity(applicationId).equals(RunModeType.YARN_APPLICATION)) {
            delApplication(clusterId, applicationId);
        }
        // session mode, delete job
        delJobTask(clusterId,applicationId,jobId);
    }

    public void clearResource(Long clusterId,String applicationId, String jobId){
        // 删除操作记录
        delOperationFromCache(jobId);
        // 删除任务缓存
        delTaskInstFromCache(clusterId, applicationId, jobId);
    }

    /**
     * update cache
     * @param taskAlertEditRequest
     * @return
     */
    public TaskAlertEditResponse updateTaskAlert(TaskAlertEditRequest taskAlertEditRequest) {
        // 根据 jobId 查找到所有部署的任务 kubePath / namespace / clusterId / runJobId
        Job job = this.jobMapper.selectById(taskAlertEditRequest.getJobId());
        // 目前只支持K8s 模式
        if(!ClusterType.YARN.equals(RunModeType.of(job.getRunModeType()).getClusterType())){
            return new TaskAlertEditResponse(ResponseStatus.FAIL,"目前只支持YARN模式");
        }
        getApplicationEntity(job.getClusterId(),job.getApplicationId()).ifPresent(applicationEntity -> {
            JobTask jobTask = applicationEntity.getJobTaskMap().get(job.getRunJobId());
            jobTask.setAlertMode(taskAlertEditRequest.getAlertMode());
            jobTask.setAlertInstanceId(taskAlertEditRequest.getAlertInstanceId());
        });

        return new TaskAlertEditResponse(ResponseStatus.SUCCESS,"");
    }
}
