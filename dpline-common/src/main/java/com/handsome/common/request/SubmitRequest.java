package com.handsome.common.request;

import com.handsome.common.options.*;

public class SubmitRequest extends RemoteRequest {

    private CheckpointOptions checkpointOptions;
    private ResourceOptions resourceOptions;
    private String flinkTaskInstanceName;
    private K8sOptions k8sOptions;
    private TaskDefinitionOptions taskDefinitionOptions;
    private RestartOptions restartOptions;
    private FlinkHomeOptions flinkHomeOptions;

    private OtherOptions otherOptions;

    public OtherOptions getOtherOptions() {
        return otherOptions;
    }

    public void setOtherOptions(OtherOptions otherOptions) {
        this.otherOptions = otherOptions;
    }
    public K8sOptions getK8sOptions() {
        return k8sOptions;
    }

    public void setK8sOptions(K8sOptions k8sOptions) {
        this.k8sOptions = k8sOptions;
    }
    public RestartOptions getRestartOptions() {
        return restartOptions;
    }

    public void setRestartOptions(RestartOptions restartOptions) {
        this.restartOptions = restartOptions;
    }

    public CheckpointOptions getCheckpointOptions() {
        return checkpointOptions;
    }

    public void setCheckpointOptions(CheckpointOptions checkpointOptions) {
        this.checkpointOptions = checkpointOptions;
    }

    public ResourceOptions getResourceOptions() {
        return resourceOptions;
    }

    public void setResourceOptions(ResourceOptions resourceOptions) {
        this.resourceOptions = resourceOptions;
    }

    public String getFlinkTaskInstanceName() {
        return flinkTaskInstanceName;
    }

    public void setFlinkTaskInstanceName(String flinkTaskInstanceName) {
        this.flinkTaskInstanceName = flinkTaskInstanceName;
    }
    public FlinkHomeOptions getFlinkHomeOptions() {
        return flinkHomeOptions;
    }

    public void setFlinkHomeOptions(FlinkHomeOptions flinkHomeOptions) {
        this.flinkHomeOptions = flinkHomeOptions;
    }
    public TaskDefinitionOptions getTaskDefinitionOptions() {
        return taskDefinitionOptions;
    }

    public void setTaskDefinitionOptions(TaskDefinitionOptions taskDefinitionOptions) {
        this.taskDefinitionOptions = taskDefinitionOptions;
    }

    public static Builder builder(){
        return new Builder();
    }



    public static class Builder {
        private OtherOptions otherOptions;
        private FlinkHomeOptions flinkHomeOptions;
        private K8sOptions k8sOptions;
        private CheckpointOptions checkpointOptions;
        private ResourceOptions resourceOptions;
        private String flinkTaskInstanceName;
        private RestartOptions restartOptions;
        private TaskDefinitionOptions taskDefinitionOptions;

        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public Builder checkpointOptions(CheckpointOptions checkpointOptions){
            this.checkpointOptions = checkpointOptions;
            return this;
        }

        public Builder resourceOptions(ResourceOptions resourceOptions){
            this.resourceOptions = resourceOptions;
            return this;
        }
        public Builder flinkTaskInstanceName(String flinkTaskInstanceName){
            this.flinkTaskInstanceName = flinkTaskInstanceName;
            return this;
        }

        public Builder restartOptions(RestartOptions restartOptions){
            this.restartOptions = restartOptions;
            return this;
        }
        public Builder k8sOptions(K8sOptions k8sOptions) {
            this.k8sOptions=k8sOptions;
            return this;
        }

        public Builder taskDefinitionOptions(TaskDefinitionOptions taskDefinitionOptions){
            this.taskDefinitionOptions = taskDefinitionOptions;
            return this;
        }

        public Builder otherOptions(OtherOptions otherOptions){
            this.otherOptions = otherOptions;
            return this;
        }

        public SubmitRequest build(){
            SubmitRequest submitRequest = new SubmitRequest();
            // 版本信息
            submitRequest.setFlinkHomeOptions(flinkHomeOptions);
            // 对外 exposedType 类型
            // 运行任务名
            submitRequest.setFlinkTaskInstanceName(flinkTaskInstanceName);
            // 镜像地址
            submitRequest.setK8sOptions(k8sOptions);
            // 重启策略
            submitRequest.setRestartOptions(restartOptions);
            // checkpoint 配置
            submitRequest.setCheckpointOptions(checkpointOptions);
            // 资源设置
            submitRequest.setResourceOptions(resourceOptions);

            submitRequest.setTaskDefinitionOptions(taskDefinitionOptions);

            submitRequest.setOtherOptions(otherOptions);
            return submitRequest;
        }

    }

}
