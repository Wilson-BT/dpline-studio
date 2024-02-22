package com.handsome.operator.job;

import com.handsome.common.enums.AlertType;
import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.RunModeType;
import com.handsome.operator.entry.IngressRulePath;
import com.handsome.operator.k8s.K8sClientManager;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 刷入队列的对象
 */
@Data
public class ClusterFlushEntity {

    /**
     * 防止指令重排，同时防止并发修改
     */
    private volatile AtomicBoolean ifOver;

    private String clusterId;

    private String nameSpace;

    private String restUrl;

    private String kubeConfigPath;

    private RunModeType runModeType;

    private IngressRulePath ingressRulePath;

    private Map<String,TaskFlushEntity> taskFlushEntityMap = new HashMap<>();

    public String getK8sClientKey() {
        return K8sClientManager.getK8sClientKey(kubeConfigPath,nameSpace);
    }
    public ClusterFlushEntity(RunModeType runMode){
        this.runModeType = runMode;
    }

    public void setTaskEntityMap(TaskFlushEntity taskFlushEntity){
        taskFlushEntityMap.put(taskFlushEntity.getJobId(),taskFlushEntity);
    }

    public void removeFromTaskEntityMap(String jobId){
        taskFlushEntityMap.remove(jobId);
    }

    public void updateTaskEntityExecStatus(String jobId,ExecStatus execStatus){
        TaskFlushEntity taskFlushEntity = taskFlushEntityMap.get(jobId);
        taskFlushEntity.setExecStatus(execStatus);
    }

    public IngressRulePath getIngressRulePath() {
        return ingressRulePath;
    }

    public void setIngressRulePath(IngressRulePath ingressRulePath) {
        this.ingressRulePath = ingressRulePath;
    }

    public AtomicBoolean getIfOver() {
        return ifOver;
    }

    public void setIfOver(boolean ifOver) {
        this.ifOver.set(ifOver);
    }

    public ClusterFlushEntity(String clusterId,
                String nameSpace,
                String restUrl,
                String kubeConfigPath,
                IngressRulePath ingressRulePath,
                RunModeType runModeType,
                              AtomicBoolean ifOver){
        this.runModeType = runModeType;
        this.clusterId = clusterId;
        this.restUrl = restUrl;
        this.nameSpace = nameSpace;
        this.kubeConfigPath = kubeConfigPath;
        this.ingressRulePath = ingressRulePath;
        this.ifOver = ifOver;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String toString() {
        return "ClusterFlushEntity{" +
            "clusterId='" + clusterId + '\'' +
            ", nameSpace='" + nameSpace + '\'' +
            ", restUrl='" + restUrl + '\'' +
            ", kubeConfigPath='" + kubeConfigPath + '\'' +
            ", runModeType=" + runModeType +
            ", taskFlushEntityMap=" + taskFlushEntityMap +
            '}';
    }

    public static class TaskFlushEntity {

        private long taskId;

        private String jobId;

        private String taskName;

        private ExecStatus execStatus;

        private AlertType alertType;

        private int alertInstanceId;

        private long currentTimeStamp;

        public TaskFlushEntity(long taskId,
                               String taskName,
                               long currentTimeStamp,
                               ExecStatus execStatus,
                               AlertType alertType,
                               int alertInstanceId,
                               String jobId
        ){
            this.taskId = taskId;
            this.taskName = taskName;
            this.execStatus = execStatus;
            this.currentTimeStamp=currentTimeStamp;
            this.alertType = alertType;
            this.alertInstanceId = alertInstanceId;
            this.jobId = jobId;
        }


        public long getCurrentTimeStamp() {
            return currentTimeStamp;
        }

        public void setCurrentTimeStamp(long currentTimeStamp) {
            this.currentTimeStamp = currentTimeStamp;
        }

        public AlertType getAlertType() {
            return alertType;
        }

        public void setAlertType(AlertType alertType) {
            this.alertType = alertType;
        }

        public int getAlertInstanceId() {
            return alertInstanceId;
        }

        public void setAlertInstanceId(int alertInstanceId) {
            this.alertInstanceId = alertInstanceId;
        }

        public long getTaskId() {
            return taskId;
        }

        public void setTaskId(long taskId) {
            this.taskId = taskId;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public ExecStatus getExecStatus() {
            return execStatus;
        }

        public void setExecStatus(ExecStatus execStatus) {
            this.execStatus = execStatus;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }

    public static class Builder{

        private AtomicBoolean ifOver;

        private String clusterId;

        private String nameSpace;

        private String restUrl;

        private String kubeConfigPath;

        private RunModeType runModeType;

        private IngressRulePath ingressRulePath;

        public Builder(){}

        public Builder clusterId(String clusterId){
            this.clusterId = clusterId;
            return this;
        }
        public Builder ingressRulePath(IngressRulePath ingressRulePath){
            this.ingressRulePath = ingressRulePath;
            return this;
        }
        public Builder nameSpace(String nameSpace){
            this.nameSpace = nameSpace;
            return this;
        }
        public Builder restUrl(String restUrl){
            this.restUrl = restUrl;
            return this;
        }
        public Builder ifOver(AtomicBoolean ifOver){
            this.ifOver = ifOver;
            return this;
        }
        public Builder kubeConfigPath(String kubeConfigPath){
            this.kubeConfigPath = kubeConfigPath;
            return this;
        }
        public Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }

        public ClusterFlushEntity build(){
            return new ClusterFlushEntity(this.clusterId,
                this.nameSpace,
                this.restUrl,
                this.kubeConfigPath,
                this.ingressRulePath,
                this.runModeType,
                this.ifOver);
        }
    }
}
