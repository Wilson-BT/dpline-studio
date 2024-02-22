package com.dpline.common.request;

import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import com.dpline.common.params.JobDefinitionOptions;
import com.dpline.common.params.K8sOptions;
import com.dpline.common.params.RuntimeOptions;
import lombok.Data;


@Data
public class FlinkK8sRemoteSubmitRequest extends FlinkRequest {

    private RuntimeOptions runtimeOptions;

    private K8sOptions k8sOptions;

    private JobDefinitionOptions jobDefinitionOptions;

    // k8s 的 配置、flink 的配置  任务的配置、运行的配置

    public JobDefinitionOptions getJobDefinitionOptions() {
        return jobDefinitionOptions;
    }

    public void setJobDefinitionOptions(JobDefinitionOptions jobDefinitionOptions) {
        this.jobDefinitionOptions = jobDefinitionOptions;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private FlinkHomeOptions flinkHomeOptions;

        private K8sOptions k8sOptions;

        private JobDefinitionOptions jobDefinitionOptions;

        private RuntimeOptions runtimeOptions;

        private RunModeType runModeType;

        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public Builder k8sOptions(K8sOptions k8sOptions){
            this.k8sOptions = k8sOptions;
            return this;
        }

        public Builder jobDefinitionOptions(JobDefinitionOptions jobDefinitionOptions){
            this.jobDefinitionOptions = jobDefinitionOptions;
            return this;
        }

        public Builder resourceOptions(RuntimeOptions runtimeOptions){
            this.runtimeOptions = runtimeOptions;
            return this;
        }

        public Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }

        public FlinkK8sRemoteSubmitRequest build(){
            FlinkK8sRemoteSubmitRequest submitRequest = new FlinkK8sRemoteSubmitRequest();
            // 版本信息
            submitRequest.setFlinkHomeOptions(flinkHomeOptions);
            // 对外 exposedType 类型
            submitRequest.setK8sOptions(k8sOptions);
            submitRequest.setRunModeType(runModeType);
            submitRequest.setJobDefinitionOptions(jobDefinitionOptions);
            submitRequest.setRuntimeOptions(runtimeOptions);
            return submitRequest;
        }

    }

}
