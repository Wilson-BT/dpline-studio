package com.dpline.common.request;


import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import lombok.Data;

import java.util.ArrayList;

@Data
public class FlinkStopRequest extends FlinkRequest {

    /**
     * cluster id
     */
    String clusterId;

    /**
     * 数据库的jobId
     */
    String runJobId;

    /**
     * if with savepoint address
     */
    Boolean withSavePointAddress;

    /**
     * save point address
     */
    String savePointAddress;

    /**
     * name space
     */
    String nameSpace;

    /**
     * kube path
     */
    String kubePath;


    Long jobId;

    String ingressHost;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        Long jobId;

        Long projectId;

        String clusterId;

        String runJobId;

        Boolean withSavePointAddress;

        String savePointAddress;

        String nameSpace;

        String kubePath;

        RunModeType runModeType;

        FlinkHomeOptions flinkHomeOptions;

        String ingressHost;

        public Builder jobId(Long jobId){
            this.jobId = jobId;
            return this;
        }
        public Builder clusterId(String clusterId){
            this.clusterId = clusterId;
            return this;
        }

        public Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }
        public Builder runJobId(String runJobId){
            this.runJobId = runJobId;
            return this;
        }
        public Builder withSavePointAddress(Boolean withSavePointAddress){
            this.withSavePointAddress = withSavePointAddress;
            return this;
        }
        public Builder savePointAddress(String savePointAddress){
            this.savePointAddress = savePointAddress;
            return this;
        }
        public Builder nameSpace(String nameSpace){
            this.nameSpace = nameSpace;
            return this;
        }
        public Builder kubePath(String kubePath){
            this.kubePath = kubePath;
            return this;
        }
        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }
        public Builder projectId(Long projectId){
            this.projectId = projectId;
            return this;
        }

        public Builder ingressHost(String ingressHost){
            this.ingressHost = ingressHost;
            return this;
        }

        public FlinkStopRequest build(){
            FlinkStopRequest flinkStopRequest = new FlinkStopRequest();
            flinkStopRequest.setJobId(this.jobId);
            // Stop 是用 rest 接口调用，不需要加载扩展jar包
            flinkStopRequest.setRunModeType(this.runModeType);
            flinkStopRequest.setNameSpace(this.nameSpace);
            flinkStopRequest.setKubePath(this.kubePath);
            flinkStopRequest.setClusterId(this.clusterId);
            flinkStopRequest.setRunJobId(this.runJobId);
            flinkStopRequest.setIngressHost(this.ingressHost);
            flinkStopRequest.setSavePointAddress(this.savePointAddress);
            flinkStopRequest.setWithSavePointAddress(this.withSavePointAddress);
            flinkStopRequest.setFlinkHomeOptions(this.flinkHomeOptions);
            return flinkStopRequest;
        }
    }
}
