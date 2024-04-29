package com.dpline.k8s.operator.job;

import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.RunModeType;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import lombok.Data;
import java.util.*;

/**
 * 刷入队列的对象
 */
@Data
public class ClusterFlushEntity {

    private String clusterId;

    private RunModeType runModeType;
    /**
     * 集群在数据库中的 Id
     */
    private Long clusterEntityId;

    /**
     * 一个cluster 内部的一个 task 的map
     */
    private Map<String, TaskFlushEntity> taskFlushEntityMap = new HashMap<>();

    public ClusterFlushEntity(RunModeType runMode){
        this.runModeType = runMode;
    }

    /**
     * 获取集群配置中的 namespace
     * @return
     */
    public String getNameSpace() {
        return K8sClusterManager.getK8sClusterParams(clusterEntityId).getNameSpace();
    }

    public String getIngressHost() {
        return K8sClusterManager.getK8sClusterParams(clusterEntityId).getIngressHost();
    }

    public String getIngressName() {
        return K8sClusterManager.getK8sClusterParams(clusterEntityId).getIngressName();
    }

    public void setTaskEntityMap(TaskFlushEntity taskFlushEntity){
        taskFlushEntityMap.put(taskFlushEntity.getRunJobId(),taskFlushEntity);
    }

    public void removeFromTaskEntityMap(String jobId){
        taskFlushEntityMap.remove(jobId);
    }

    public void updateTaskEntityExecStatus(String jobId,ExecStatus execStatus){
        TaskFlushEntity taskFlushEntity = taskFlushEntityMap.get(jobId);
        taskFlushEntity.setExecStatus(execStatus);
    }


    public ClusterFlushEntity(String clusterId,
                              RunModeType runModeType,
                              Long clusterEntityId){
        this.runModeType = runModeType;
        this.clusterId = clusterId;
        this.clusterEntityId = clusterEntityId;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String toString() {
        return "ClusterFlushEntity{" +
                "clusterId='" + clusterId + '\'' +
                ", runModeType=" + runModeType +
                ", clusterEntityId=" + clusterEntityId +
                ", taskFlushEntityMap=" + taskFlushEntityMap +
                '}';
    }


    public static class Builder{

        private String clusterId;
        private RunModeType runModeType;
        private Long clusterEntityId;

        public Builder(){}

        public Builder clusterId(String clusterId){
            this.clusterId = clusterId;
            return this;
        }
        public Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }
        public Builder clusterEntityId(Long clusterEntityId){
            this.clusterEntityId = clusterEntityId;
            return this;
        }

        public ClusterFlushEntity build(){
            return new ClusterFlushEntity(
                    this.clusterId,
                    this.runModeType,
                    this.clusterEntityId);
        }
    }
}
