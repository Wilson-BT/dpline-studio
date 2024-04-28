package com.dpline.common.request;

import com.dpline.common.enums.AlertMode;
import lombok.Builder;
import lombok.Data;

/**
 * jobId
 */
@Data
@Builder
public class TaskAlertEditRequest implements Request {

    /**
     * jobId
     */
    private Long jobId;

    /**
     * 任务名称,或者session Name
     */
    private String clusterId;

    /**
     * run job id
     */
    private String runJobId;

    /**
     * 告警模式
     */
    private AlertMode alertMode;

    /**
     * 告警实例
     */
    private Long alertInstanceId;

    public TaskAlertEditRequest(Long jobId, String clusterId,
                                String runJobId, AlertMode alertMode,
                                Long alertInstanceId){
        this.clusterId = clusterId;
        this.runJobId = runJobId;
        this.jobId = jobId;
        this.alertMode = alertMode;
        this.alertInstanceId = alertInstanceId;
    }

    public TaskAlertEditRequest(){}
}
