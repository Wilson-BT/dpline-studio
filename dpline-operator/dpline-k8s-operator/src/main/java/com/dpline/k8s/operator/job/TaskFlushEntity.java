package com.dpline.k8s.operator.job;

import com.dpline.common.enums.AlertMode;
import com.dpline.common.enums.ExecStatus;
import lombok.Data;

@Data
public class TaskFlushEntity {

    private Long jobId;

    private String runJobId;

    private String taskName;

    private ExecStatus execStatus;

    private AlertMode alertMode;

    private Long alertInstanceId;

    private Long currentTimeStamp;

    public TaskFlushEntity(Long jobId,
                           String taskName,
                           Long currentTimeStamp,
                           ExecStatus execStatus,
                           AlertMode alertMode,
                           Long alertInstanceId,
                           String runJobId

    ) {
        this.jobId = jobId;
        this.taskName = taskName;
        this.execStatus = execStatus;
        this.currentTimeStamp = currentTimeStamp;
        this.alertMode = alertMode;
        this.alertInstanceId = alertInstanceId;
        this.runJobId = runJobId;
    }


    public TaskFlushEntity() {
    }
}
