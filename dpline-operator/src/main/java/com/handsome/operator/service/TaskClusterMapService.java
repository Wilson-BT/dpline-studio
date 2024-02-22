package com.handsome.operator.service;

import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.OperationsEnum;
import com.handsome.common.enums.RunModeType;
import com.handsome.common.util.Asserts;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.entity.FlinkSession;
import com.handsome.dao.mapper.FlinkSessionMapper;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import com.handsome.common.util.TaskPath;
import com.handsome.operator.entry.IngressRulePath;
import com.handsome.operator.job.ClusterFlushEntity;
import com.handsome.operator.k8s.K8sClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Service
public class TaskClusterMapService extends BaseService {

    @Autowired
    FlinkSessionMapper flinkSessionMapper;

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    TaskIngressService taskIngressService;

    @Autowired
    HashMap<String,ConcurrentHashMap<String,ClusterFlushEntity>> clientKubePathMap;

    ConcurrentHashMap<String, OperationsEnum> operationMap = new ConcurrentHashMap<>();

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
     *
     * @param runTaskInstance
     */
    public ClusterFlushEntity newTaskAddToCache(FlinkRunTaskInstance runTaskInstance) {
        // 如果是 application 模式
        String k8sClientKey = K8sClientManager.getK8sClientKey(
            runTaskInstance.getKubePath(),
            runTaskInstance.getNameSpace());
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap =
            clientKubePathMap.get(k8sClientKey);
        if (Asserts.isNull(clusterIdEntityMap)) {
            clusterIdEntityMap = new ConcurrentHashMap<>();
        }
        ClusterFlushEntity clusterFlushEntity;
        ClusterFlushEntity.TaskFlushEntity taskFlushEntity = new ClusterFlushEntity.TaskFlushEntity(
            runTaskInstance.getId(),
            runTaskInstance.getFlinkTaskInstanceName(),
            System.currentTimeMillis(),
            runTaskInstance.getExecStatus(),
            runTaskInstance.getAlertType(),
            runTaskInstance.getAlertInstanceId(),
            runTaskInstance.getJobId()
        );
        if (runTaskInstance.getRunMode().equals(RunModeType.K8S_APPLICATION)) {
            clusterFlushEntity = ClusterFlushEntity.builder()
                .runModeType(RunModeType.K8S_APPLICATION)
                .clusterId(runTaskInstance.getFlinkTaskInstanceName())
                .kubeConfigPath(runTaskInstance.getKubePath())
                .restUrl(TaskPath.getRestUrlPath(runTaskInstance.getFlinkTaskInstanceName()))
                .nameSpace(runTaskInstance.getNameSpace())
                .ifOver(new AtomicBoolean(true))
                .ingressRulePath(new IngressRulePath(TaskPath.getServiceName(runTaskInstance.getFlinkTaskInstanceName()), 8081))
                .build();
        } else {
//          String flinkSessionName = runTaskInstance.getFlinkSessionName();
            // 如果有这个clusterId，那么就直接
            ClusterFlushEntity clusterInst = clusterIdEntityMap.get(runTaskInstance.getFlinkSessionName());
            clusterFlushEntity = clusterInst == null ?
                ClusterFlushEntity.builder()
                    .runModeType(RunModeType.K8S_SESSION)
                    .clusterId(runTaskInstance.getFlinkSessionName())
                    .kubeConfigPath(runTaskInstance.getKubePath())
                    .restUrl(TaskPath.getRestUrlPath(runTaskInstance.getFlinkSessionName()))
                    .nameSpace(runTaskInstance.getNameSpace())
                    .ifOver(new AtomicBoolean(true))
                    .ingressRulePath(new IngressRulePath(TaskPath.getServiceName(runTaskInstance.getFlinkSessionName()), 8081))
                    .build()
                : clusterInst;
        }
        assert clusterFlushEntity != null;
        clusterFlushEntity.setTaskEntityMap(taskFlushEntity);
        try {
            clusterIdEntityMap.put(clusterFlushEntity.getClusterId(), clusterFlushEntity);
            clientKubePathMap.put(k8sClientKey, clusterIdEntityMap);
            logger.info("New task [{}], Mode [{}] in ClusterId [{}] has add into cache.",
                runTaskInstance.getFlinkTaskInstanceName(),
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
            String k8sClientKey = K8sClientManager.getK8sClientKey(flinkSession.getKubePath(), flinkSession.getNameSpace());
            ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap =
                clientKubePathMap.get(k8sClientKey);
            if (Asserts.isNull(clusterIdEntityMap)) {
                clusterIdEntityMap = new ConcurrentHashMap<>();
            }
            // if exist,that`s created when cache task
            String flinkSessionName = flinkSession.getFlinkSessionName();
            if (Asserts.isNotNull(clusterIdEntityMap.get(flinkSessionName))) {
                return;
            }
            ClusterFlushEntity clusterFlushEntity = ClusterFlushEntity.builder()
                .runModeType(RunModeType.K8S_SESSION)
                .clusterId(flinkSession.getFlinkSessionName())
                .kubeConfigPath(flinkSession.getKubePath())
                .restUrl(TaskPath.getRestUrlPath(flinkSessionName))
                .nameSpace(flinkSession.getNameSpace())
                .ifOver(new AtomicBoolean(true))
                .ingressRulePath(new IngressRulePath(TaskPath.getServiceName(flinkSession.getFlinkSessionName()), 8081))
                .build();
            clusterIdEntityMap.put(flinkSession.getFlinkSessionName(), clusterFlushEntity);
            clientKubePathMap.put(k8sClientKey, clusterIdEntityMap);
        });
    }

    /**
     * flush and reFlush to cache
     *
     * @return
     */
    public synchronized void batchCacheAllRunningTaskInst() {
        logger.info("Fetch running flink task and cache it.");
        List<FlinkRunTaskInstance> flinkRunTaskInstances = flinkTaskInstanceMapper.selectByFlinkInstanceStatus(
            ExecStatus.runningArray());
        clientKubePathMap.clear();
        flinkRunTaskInstances.forEach(new Consumer<FlinkRunTaskInstance>() {
            @Override
            public void accept(FlinkRunTaskInstance runTaskInstance) {
                newTaskAddToCache(runTaskInstance);
            }
        });
    }

    /**
     * 创建 session 的时候就需要创建 ingress，删除 session 的时候才能删除ingress 和 map
     * 不清理session 的map缓存，也不清理ingress, 只有在删除 session 的时候清理
     *
     * @param k8sClientKey
     * @param jobId
     */
    public void delSessionInstFromClusterMap(String k8sClientKey, String clusterId, String jobId) {
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(k8sClientKey);
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
     * @param k8sClientKey
     * @param clusterId
     */
    public void delApplicationInstFromClusterMap(String k8sClientKey, String clusterId) {
        ConcurrentHashMap<String, ClusterFlushEntity> clusterIdEntityMap = clientKubePathMap.get(k8sClientKey);
        if (Asserts.isNull(clusterIdEntityMap)) {
            return;
        }
        ClusterFlushEntity removedClusterFlushEntity = clusterIdEntityMap.remove(clusterId);
        logger.info("ClusterId:[{}] has been deleted from clusterId IngressPath Map", clusterId);

//        // if only one application leave in clusterIdEntityMap,no session.
//        // we can say flink session is delete from dpline
//        // need delete Ingress
//        // 因为session模式不会占用空间，理论上不会
//        if (clusterIdEntityMap.isEmpty()) {
//            taskIngressService.delIngress(clusterId, k8sClientKey);
//        }

        taskIngressService.delIngressRule(clusterId, k8sClientKey, removedClusterFlushEntity);
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
            = clientKubePathMap.get(clusterFlushEntity.getK8sClientKey());
        if (Asserts.isNull(clusterIdEntityMap)) {
            clusterIdEntityMap = new ConcurrentHashMap<>();
        }
        clusterIdEntityMap.putIfAbsent(clusterFlushEntity.getClusterId(), clusterFlushEntity);
        clientKubePathMap.put(clusterFlushEntity.getK8sClientKey(),clusterIdEntityMap);
    }

    public Optional<ClusterFlushEntity> getClusterEntity(String kubePathKey,String clusterId){
        if (clientKubePathMap.containsKey(kubePathKey)){
            return Optional.ofNullable(clientKubePathMap.get(kubePathKey).get(clusterId));
        }
        return Optional.empty();
    }

    /**
     * 是否包含这个clusterId
     *
     * @param clusterFlushEntity
     * @return
     */
    public boolean containClusterEntity(ClusterFlushEntity clusterFlushEntity){
        String clusterId = clusterFlushEntity.getClusterId();
        String k8sClientKey = clusterFlushEntity.getK8sClientKey();
        ConcurrentHashMap<String, ClusterFlushEntity> kubePathClusterFlushEntityConcurrentHashMap = clientKubePathMap.get(k8sClientKey);
        return kubePathClusterFlushEntityConcurrentHashMap.containsKey(clusterId);
    }

}
