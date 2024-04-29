package com.dpline.k8s.operator.watcher;

import com.dpline.common.enums.ExecStatus;
import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class K8sSessionStatusRemoteTrack extends K8sStatusRemoteTrack {

    K8sClusterManager k8sClusterManager;

    private Logger logger = LoggerFactory.getLogger(K8sSessionStatusRemoteTrack.class);

    public K8sSessionStatusRemoteTrack(K8sClusterManager k8sClusterManager) {
        this.k8sClusterManager = k8sClusterManager;
    }

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

    /**
     * TODO Session remote 如何重新定义
     * @param clusterInst
     * @return
     */
    public ExecStatus doRemoteToK8s(ClusterFlushEntity clusterInst) {
        ExecStatus execStatus = ExecStatus.NONE;
        try {
            boolean deploymentExists = k8sClusterManager.isDeploymentExists(clusterInst);
            logger.info("Deployment exist status is [{}]", deploymentExists);
            // if not exist, return NONE
            if (!deploymentExists) {
                return execStatus;
            }
            // 如果存在deployment，说明没有删除成功，或者刚刚提交，rest ui 未生效
            Optional<ContainerStatus> podsStatus = k8sClusterManager.getPodsStatus(
                k8sClusterManager.getPods(clusterInst)
            );
            return podsStatus.map(containerStatus -> {
                boolean podRunningNow = k8sClusterManager.isPodRunningNow(containerStatus);
                // not terminated,running
                if (podRunningNow) {
                    logger.warn("The task [{}] is running on k8s.please check your rest-url.",clusterInst.getClusterId());
                    return ExecStatus.RUNNING;
                }
                return execStatus;
            }).orElse(execStatus);
            // 根据现在的
        } catch (Exception e){
            e.printStackTrace();
        }
        return execStatus;
    }
}
