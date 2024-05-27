package com.dpline.common.request;


import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.*;
import lombok.Builder;
import lombok.Data;


@Data
public class YarnRemoteSubmitRequest extends FlinkSubmitRequest {

    private YarnOptions yarnOptions;

    public YarnRemoteSubmitRequest(){}
    public static YarnRemoteSubmitRequest.Builder builder(){
        return new YarnRemoteSubmitRequest.Builder();
    }

    public static class Builder {

        private FlinkHomeOptions flinkHomeOptions;

        private YarnOptions yarnOptions;

        private JobDefinitionOptions jobDefinitionOptions;

        private RuntimeOptions runtimeOptions;

        private RunModeType runModeType;

        public YarnRemoteSubmitRequest.Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public YarnRemoteSubmitRequest.Builder yarnOptions(YarnOptions yarnOptions){
            this.yarnOptions = yarnOptions;
            return this;
        }

        public YarnRemoteSubmitRequest.Builder jobDefinitionOptions(JobDefinitionOptions jobDefinitionOptions){
            this.jobDefinitionOptions = jobDefinitionOptions;
            return this;
        }

        public YarnRemoteSubmitRequest.Builder resourceOptions(RuntimeOptions runtimeOptions){
            this.runtimeOptions = runtimeOptions;
            return this;
        }

        public YarnRemoteSubmitRequest.Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }

        public YarnRemoteSubmitRequest build(){
            YarnRemoteSubmitRequest submitRequest = new YarnRemoteSubmitRequest();
            // 版本信息
            submitRequest.setFlinkHomeOptions(flinkHomeOptions);
            // 对外 exposedType 类型
            submitRequest.setYarnOptions(yarnOptions);
            submitRequest.setRunModeType(runModeType);
            submitRequest.setJobDefinitionOptions(jobDefinitionOptions);
            submitRequest.setRuntimeOptions(runtimeOptions);
            return submitRequest;
        }

    }
}
