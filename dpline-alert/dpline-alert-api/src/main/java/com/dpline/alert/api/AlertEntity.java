package com.dpline.alert.api;

import lombok.Data;

@Data
public class AlertEntity {

    private long alterInstanceId;

    private long jobId;

    private String taskName;

    private  String taskStatusBefore;

    private  String taskStatusCurrent;

    public AlertEntity(long alterInstanceId, long jobId, String taskName, String taskStatusBefore, String taskStatusCurrent) {
        this.jobId = jobId;
        this.alterInstanceId = alterInstanceId;
        this.taskName = taskName;
        this.taskStatusBefore = taskStatusBefore;
        this.taskStatusCurrent = taskStatusCurrent;
    }

}
