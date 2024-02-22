package com.dpline.operator.watcher;

import com.dpline.common.enums.ExecStatus;
import com.dpline.operator.job.ClusterFlushEntity;
import com.dpline.operator.job.TaskFlushEntity;
import com.dpline.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class K8sApplicationStatusRemoteTrack extends K8sStatusRemoteTrack {

    public K8sApplicationStatusRemoteTrack(K8sClusterManager k8sClusterManager) {
        this.k8sClusterManager = k8sClusterManager;
    }

    private K8sClusterManager k8sClusterManager;


    private Logger logger = LoggerFactory.getLogger(K8sApplicationStatusRemoteTrack.class);


    @Override
    public Map<String, ExecStatus> remote(ClusterFlushEntity clusterFlushEntity) {
        ExecStatus execStatus = doRemoteToK8s(clusterFlushEntity);
        HashMap<String,ExecStatus> map = new HashMap<>();
        clusterFlushEntity.getTaskFlushEntityMap().forEach(
            (key,value)->{
                map.put(key,execStatus);
            }
        );
        return map;
    }

    public ExecStatus doRemoteToK8s(ClusterFlushEntity clusterInst) {
        ExecStatus execStatus = ExecStatus.NONE;
        try {
            boolean deploymentExists = k8sClusterManager.isDeploymentExists(clusterInst);
            logger.info("Deployment exist status is [{}]", deploymentExists);
            // if not exist
            if (!deploymentExists) {
                return inferTaskStatusFromCacheBefore(clusterInst);
            }
            // 如果存在deployment，说明没有删除成功，或者刚刚提交，rest ui 未生效
            Optional<ContainerStatus> podsStatus = k8sClusterManager.getPodsStatus(
                k8sClusterManager.getPods(clusterInst)
            );
            // PodsStatus 不存在的话 ，判断为初始化状态
            return podsStatus.map(containerStatus -> {
                boolean podRunningNow = k8sClusterManager.isPodRunningNow(containerStatus);
                boolean podTerminatedBefore = k8sClusterManager.isPodTerminatedBefore(containerStatus);
                // not terminated,running
                if (podRunningNow) {
                    logger.warn("The task [{}] is running on k8s.please check your rest-url.",clusterInst.getClusterId());
                    return ExecStatus.RUNNING;
                }
                if (podTerminatedBefore) {
                    // delete deployment,
                    logger.error("ContainerStatus:[{}].", containerStatus.toString());
                    logger.error("The task is failed before. And not run now. Ready to delete deployment and configmaps.");
                    k8sClusterManager.deleteDeployment(clusterInst);
                    k8sClusterManager.deleteConfigMaps(clusterInst.getClusterEntityId(),clusterInst.getClusterId());
                    return ExecStatus.FAILED;
                }
                return ExecStatus.INITIALIZING;
            }).orElse(ExecStatus.INITIALIZING);
        } catch (Exception e){
            e.printStackTrace();
        }
        return execStatus;
    }

    private ExecStatus inferTaskStatusFromCacheBefore(ClusterFlushEntity clusterInst) throws Exception {
        Map<String, TaskFlushEntity> taskFlushEntityMap = clusterInst.getTaskFlushEntityMap();
        List<TaskFlushEntity> taskFlushEntityList = new ArrayList<>(taskFlushEntityMap.values());
        TaskFlushEntity taskFlushEntity = taskFlushEntityList.get(0);
        ExecStatus execStatus = ExecStatus.NONE;
        if (taskFlushEntity.getExecStatus().equals(ExecStatus.FAILING)) {
            execStatus = ExecStatus.FAILED;
        }
        if (taskFlushEntity.getExecStatus().equals(ExecStatus.CANCELLING)) {
            execStatus = ExecStatus.CANCELED;
        }
        logger.info("Infer task status from cache before is None.");
        return execStatus;
    }



}
