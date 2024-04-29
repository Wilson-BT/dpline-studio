//package com.dpline.operator.service;
//
//import com.dpline.common.enums.ExecStatus;
//import com.dpline.operator.job.ClusterFlushEntity;
//import io.fabric8.kubernetes.api.model.ContainerStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//public class ApplicationStatusDiscover extends StatusDiscover {
//
//    @Autowired
//    TaskRemoteService taskRemoteService;
//
//    @Override
//    void remoteStatus(ClusterFlushEntity clusterFlushEntity) {
//        ExecStatus execStatus = doRemoteToK8s(clusterFlushEntity);
//        clusterFlushEntity.getTaskFlushEntityMap().forEach((jobId, taskFlushEntity) -> {
//            taskStatusService.updateTaskInstStatus(clusterInst, jobId, execStatus);
//        });
//    }
//
//    @Override
//    void restUrlStatus(ClusterFlushEntity clusterFlushEntity) {
//
//    }
//
//    @Override
//    void updateDB(ClusterFlushEntity clusterFlushEntity) {
//
//    }
//
//
//    public ExecStatus doRemoteToK8s(ClusterFlushEntity clusterInst) {
//        ExecStatus execStatus = ExecStatus.NONE;
//        try {
//            boolean deploymentExists = k8sClusterManager.isDeploymentExists(
//                clusterInst.getK8sClientKey(),
//                clusterInst.getClusterId(),
//                clusterInst.getNameSpace());
//            // if not exist
//            if (!deploymentExists) {
//                return inferTaskStatusFromCacheBefore(clusterInst);
//            }
//
//            Optional<ContainerStatus> podsStatus = k8sClusterManager.getPodsStatus(k8sClusterManager.getPods(clusterInst.getK8sClientKey(),
//                clusterInst.getClusterId(), clusterInst.getNameSpace()));
//            boolean podTerminatedBefore = k8sClusterManager.isPodTerminatedBefore(podsStatus);
//            boolean podRunningNow = k8sClusterManager.isPodRunningNow(podsStatus);
//
//            // not terminated,running
//            if (podRunningNow) {
//                // retrieve flink cluster client
////                String deploymentRestUrl = TaskPath.getRestUrlPath(clusterInst.getClusterId());
////                clusterInst.setRestUrl(deploymentRestUrl);
////                //  直接更新rest-url,session模式，需要根据 session id，去批量更新
////                if(clusterInst.getRunModeType().equals(RunModeType.K8S_APPLICATION)){
////                    ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterInst.getTaskFlushEntityMap().get(clusterInst.getClusterId());
////                    flinkTaskInstanceMapper.updateRestUrl(taskFlushEntity.getTaskId(),deploymentRestUrl);
////                } else {
////                    // 更新session模式下的所有的 task 的url
////                    ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterInst.getTaskFlushEntityMap().get(clusterInst.getClusterId());
////                    flinkTaskInstanceMapper.batchUpdateSessionRestUrl(taskFlushEntity.getTaskId(),deploymentRestUrl);
//                logger.warn("The task [{}] is running on k8s.please check your rest-url.",clusterInst.getClusterId());
//
////                Map<String, ClusterFlushEntity.TaskFlushEntity> taskFlushEntityMap = clusterInst.getTaskFlushEntityMap();
////                taskFlushEntityMap.forEach();
//                return ExecStatus.RUNNING;
//            }
//
//            // Terminated Before
//            if (podTerminatedBefore) {
//                // jobManager down, write log logger.error("");
//                String logPath = k8sClusterManager.writeDeploymentLog(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
//                // delete deployment,
//                k8sClusterManager.deleteDeployment(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
//                k8sClusterManager.deleteConfigMaps(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
////                k8sClusterManager.deleteDeployment(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
//                logger.warn("The task is failed before.Delete Deployment and write log to [{}]",logPath);
//                return ExecStatus.FAILED;
//            }
//            return ExecStatus.INITIALIZING;
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return execStatus;
//    }
//
//
//}
