package com.dpline.operator.service;

import com.dpline.common.enums.*;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.request.TaskAlertEditRequest;
import com.dpline.common.request.TaskAlertEditResponse;
import com.dpline.common.util.Asserts;
import com.dpline.dao.entity.Cluster;
import com.dpline.dao.entity.FlinkSession;
import com.dpline.dao.entity.Job;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.dao.mapper.FlinkSessionMapper;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.operator.entry.SniffEvent;
import com.dpline.operator.job.ClusterFlushEntity;
import com.dpline.operator.job.TaskFlushEntity;
import com.dpline.operator.k8s.K8sClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class TaskClusterMapService extends BaseService {

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    JobMapper jobMapper;

    @Autowired
    ClusterMapper clusterMapper;

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    HashMap<Long,ConcurrentHashMap<String, ClusterFlushEntity>> clientKubePathMap;

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
     * 统一缓存任务 添加入口，
     */
    public ClusterFlushEntity newTaskAddToCache(Job job) {
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap =
            clientKubePathMap.get(job.getClusterId());
        if (Asserts.isNull(clusterIdEntityMap)) {
            clusterIdEntityMap = new ConcurrentHashMap<>();
        }
        ClusterFlushEntity clusterFlushEntity;
        long currentTimeMillis = System.currentTimeMillis();

//        } else {
////          String flinkSessionName = runTaskInstance.getFlinkSessionName();
//            // 如果有这个clusterId，那么就直接
//            ClusterFlushEntity clusterInst = clusterIdEntityMap.get(job.getFlinkSessionName());
//            clusterFlushEntity = clusterInst == null ?
//                ClusterFlushEntity.builder()
//                    .runModeType(RunModeType.K8S_SESSION)
//                    .clusterId(job.getFlinkSessionName())
//                    .kubeConfigPath(k8sClusterParams.getKubePath())
//                    .restUrl(TaskPath.getRestUrlPath(job.getFlinkSessionName()))
//                    .nameSpace(k8sClusterParams.getNameSpace())
//                    .ifOver(new AtomicBoolean(true))
//                    .ingressRulePath(new IngressRulePath(TaskPath.getServiceName(job.getFlinkSessionName()), 8081))
//                    .build()
//                : clusterInst;
//        }
//        assert clusterFlushEntity != null;
        try {
            ExecStatus execStatus = ExecStatus.of(job.getExecStatus());
            AlertMode alertMode = AlertMode.of(job.getAlertMode());
            logger.info("CurrentTimeMillis:[{}], execStatus:[{}], alertMode:[{}], job:[{}]",currentTimeMillis,execStatus,alertMode,job.toString());
            TaskFlushEntity taskFlushEntity = new TaskFlushEntity(
                    job.getId(),
                    job.getJobName(),
                    currentTimeMillis,
                    execStatus,
                    alertMode,
                    job.getAlertInstanceId(),
                    job.getRunJobId()
            );

            clusterFlushEntity = ClusterFlushEntity
                    .builder()
                    .runModeType(RunModeType.K8S_APPLICATION)
                    .clusterId(job.getJobName())
                    .clusterEntityId(job.getClusterId())
                    .build();
            clusterFlushEntity.setTaskEntityMap(taskFlushEntity);
            // key=集群名称，value=集群明细
            clusterIdEntityMap.put(clusterFlushEntity.getClusterId(), clusterFlushEntity);
            // 哪个集群下？
            clientKubePathMap.put(clusterFlushEntity.getClusterEntityId(), clusterIdEntityMap);
            logger.info("New task [{}], run mode [{}] in clusterId [{}] has add into cache.",
                job.getJobName(),
                clusterFlushEntity.getRunModeType().getValue(),
                clusterFlushEntity.getClusterId()
            );
            return clusterFlushEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缓存 session ,
     */
    public void batchCacheAllRunningSessionInst() {
        List<FlinkSession> flinkSessions = flinkSessionMapper.queryAllOnlineFlinkSession();
        flinkSessions.forEach(flinkSession -> {
            Long kubernetesClusterId = flinkSession.getKubernetesClusterId();
            ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap =
                clientKubePathMap.get(kubernetesClusterId);
            if (Asserts.isNull(clusterIdEntityMap)) {
                clusterIdEntityMap = new ConcurrentHashMap<>();
            }
            // if exist,that`s created when cache task
            String flinkSessionName = flinkSession.getFlinkSessionName();
            if (Asserts.isNotNull(clusterIdEntityMap.get(flinkSessionName))) {
                return;
            }
            ClusterFlushEntity clusterFlushEntity = ClusterFlushEntity
                    .builder()
                    .runModeType(RunModeType.K8S_SESSION)
                    .clusterId(flinkSession.getFlinkSessionName())
                    .clusterEntityId(flinkSession.getKubernetesClusterId())
                .build();
            clusterIdEntityMap.put(flinkSession.getFlinkSessionName(), clusterFlushEntity);
            clientKubePathMap.put(kubernetesClusterId, clusterIdEntityMap);
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
        clientKubePathMap.clear();
        // add to cache
        jobList.forEach(new Consumer<Job>() {
            @Override
            public void accept(Job job) {
                newTaskAddToCache(job);
            }
        });
        // add to sniff
        ArrayList<SniffEvent> sniffArrayList = new ArrayList<>();
        clientKubePathMap.forEach(
            (kubeKey,clusterMap)->{
                clusterMap.forEach(
                    (clusterId,clusterEntity)->{
                        sniffArrayList.add(new SniffEvent(clusterEntity.getClusterEntityId(),clusterId));
                    }
                );
            }
        );
        return sniffArrayList;
    }

    /**
     * 创建 session 的时候就需要创建 ingress，删除 session 的时候才能删除ingress 和 map
     * 不清理session 的map缓存，也不清理ingress, 只有在删除 session 的时候清理
     *
     * @param clusterEntityId
     * @param jobId
     */
    public void delSessionInstFromClusterMap(Long clusterEntityId, String clusterId, String jobId) {
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(clusterEntityId);
        if (Asserts.isNull(clusterIdEntityMap)) {
            return;
        }
        ClusterFlushEntity clusterFlushEntity = clusterIdEntityMap.get(clusterId);
        if (Asserts.isNotNull(clusterFlushEntity)) {
            clusterFlushEntity.removeFromTaskEntityMap(jobId);
        }
    }

    /**
     * 删除 application 的缓存
     *
     * @param clusterEntityId
     * @param clusterId
     */
    public void delApplicationInstFromClusterMap(Long clusterEntityId, String clusterId) {
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(clusterEntityId);
        if (Asserts.isNull(clusterIdEntityMap)) {
            return;
        }
        clusterIdEntityMap.remove(clusterId);
        logger.info("ClusterId:[{}] has been deleted from clusterId IngressPath Map", clusterId);
    }

    /**
     * 更新 clusterMap
     * key: k8s-namespace
     * value:
     *      cluster-id
     *      clusterEntity
     *
     * @param clusterFlushEntity
     */
    public void addOrUpdateClusterMap(ClusterFlushEntity clusterFlushEntity){
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap
            = clientKubePathMap.get(clusterFlushEntity.getClusterEntityId());
        if (Asserts.isNull(clusterIdEntityMap)) {
            clusterIdEntityMap = new ConcurrentHashMap<>();
        }
        clusterIdEntityMap.putIfAbsent(clusterFlushEntity.getClusterId(), clusterFlushEntity);
        clientKubePathMap.put(clusterFlushEntity.getClusterEntityId(),clusterIdEntityMap);
    }

    public Optional<ClusterFlushEntity> getClusterEntity(Long clusterEntityId,String clusterId){
        if (clientKubePathMap.containsKey(clusterEntityId)){
            return Optional.ofNullable(clientKubePathMap.get(clusterEntityId).get(clusterId));
        }
        return Optional.empty();
    }

    public Optional<ConcurrentHashMap<String, ClusterFlushEntity>> getClusterIdEntityMap(Long clusterEntityId){
        return Optional.ofNullable(clientKubePathMap.get(clusterEntityId));
    }



    /**
     * 从缓存中删除对象
     * 如果是application 模式，直接删除 cluster对象
     * 如果是session 模式，删除 内部任务
     *
     * @param clusterFlushEntity
     * @param jobId
     */
    public void delTaskInstFromCache(ClusterFlushEntity clusterFlushEntity, String jobId) {
        if (clusterFlushEntity.getRunModeType().equals(RunModeType.K8S_APPLICATION)) {
            delApplicationInstFromClusterMap(clusterFlushEntity.getClusterEntityId(), clusterFlushEntity.getClusterId());
        }
        delSessionInstFromClusterMap(clusterFlushEntity.getClusterEntityId(), clusterFlushEntity.getClusterId(), jobId);
    }

    public void clearResource(ClusterFlushEntity clusterInst,String jobId){
        // 删除操作记录
        delOperationFromCache(jobId);
        // 删除任务缓存
        delTaskInstFromCache(clusterInst, jobId);
    }

    /**
     * 更新 K8s 缓存
     * @param taskAlertEditRequest
     * @return
     */
    public TaskAlertEditResponse updateTaskAlert(TaskAlertEditRequest taskAlertEditRequest) {
        // 根据 jobId 查找到所有部署的任务 kubePath / namespace / clusterId / runJobId
        Job job = this.jobMapper.selectById(taskAlertEditRequest.getJobId());
        Cluster cluster = this.clusterMapper.selectById(job.getClusterId());
        // 目前只支持K8s 模式
        if(!ClusterType.KUBERNETES.equals(ClusterType.of(cluster.getClusterType()))){
            return new TaskAlertEditResponse(ResponseStatus.FAIL,"目前只支持K8S模式");
        }
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntity = clientKubePathMap.get(cluster.getId());
        if(Asserts.isNotNull(clusterIdEntity)){
            ClusterFlushEntity clusterFlushEntity = clusterIdEntity.get(taskAlertEditRequest.getClusterId());
            if(Asserts.isNull(clusterFlushEntity)){
                return new TaskAlertEditResponse(ResponseStatus.FAIL,"集群未在运行，请检查");
            }
            TaskFlushEntity taskFlushEntity = clusterFlushEntity.getTaskFlushEntityMap().get(taskAlertEditRequest.getRunJobId());
            taskFlushEntity.setAlertMode(taskAlertEditRequest.getAlertMode());
            taskFlushEntity.setAlertInstanceId(taskAlertEditRequest.getAlertInstanceId());
        }
        return new TaskAlertEditResponse(ResponseStatus.SUCCESS,"");
    }
}
