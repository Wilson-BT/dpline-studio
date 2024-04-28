package com.dpline.common.request;

import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper=false)
public class FlinkTriggerRequest extends FlinkRequest {

    /**
     * cluster id, 做路径
     */
    String clusterId;

    /**
     * 数据库的jobId -> taskId，做hash
     */
    String runJobId;

    /**
     * run mode
     */
    RunModeType runModeType;

    /**
     * save point address
     */
    String savePointAddress;

    String nameSpace;

    Long jobId;

    String ingressHost;

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        long jobId;

        String clusterId;

        String runJobId;

        RunModeType runModeType;

        String savePointAddress;

        String nameSpace;

        String ingressHost;

        FlinkHomeOptions flinkHomeOptions;

        public Builder jobId(long jobId){
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

        public Builder savePointAddress(String savePointAddress){
            this.savePointAddress = savePointAddress;
            return this;
        }

        public Builder nameSpace(String nameSpace){
            this.nameSpace = nameSpace;
            return this;
        }

        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public Builder ingressHost(String ingressHost){
            this.ingressHost = ingressHost;
            return this;
        }

        public FlinkTriggerRequest build(){
            FlinkTriggerRequest flinkTriggerRequest = new FlinkTriggerRequest();
            flinkTriggerRequest.setJobId(this.jobId);
            // 触发 savepoint 不需要 加载额外的类属性
            flinkTriggerRequest.setFlinkHomeOptions(flinkHomeOptions);
            flinkTriggerRequest.setRunJobId(this.runJobId);
            flinkTriggerRequest.setClusterId(this.clusterId);
            flinkTriggerRequest.setRunModeType(this.runModeType);
            flinkTriggerRequest.setSavePointAddress(this.savePointAddress);
            flinkTriggerRequest.setIngressHost(this.ingressHost);
            flinkTriggerRequest.setNameSpace(nameSpace);
            return flinkTriggerRequest;
        }
    }
}
