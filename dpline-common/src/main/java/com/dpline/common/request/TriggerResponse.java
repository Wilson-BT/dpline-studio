package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class TriggerResponse extends Response {

    private long jobId;

    private String savePointAddress;

    public TriggerResponse(){}

    public TriggerResponse(long taskId, String savePointAddress, ResponseStatus responseStatus) {
        this.jobId = taskId;
        this.savePointAddress = savePointAddress;
        this.responseStatus = responseStatus;
    }

}
