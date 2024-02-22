package com.handsome.common.request;

import com.handsome.common.enums.ResponseStatus;
import com.handsome.common.enums.RunModeType;
import lombok.Data;

@Data
public class TriggerResponse extends RemoteRequest {

    private long taskId;

    private ResponseStatus responseStatus;

    private String savePointAddress;

    /**
     * 保存在数据库中，用于stop任务需要
     */
    private String jobId;

    public TriggerResponse(){}

    public TriggerResponse(long taskId, String savePointAddress, ResponseStatus responseStatus) {
        this.taskId = taskId;
        this.savePointAddress = savePointAddress;
        this.responseStatus = responseStatus;
    }

}
