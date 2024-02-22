package com.handsome.common.request;

import com.handsome.common.enums.ResponseStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitResponse implements Response, Serializable {

    private String clusterId;

    private Integer port;

    private Long taskId;

    private String restUrl;

    private ResponseStatus responseStatus;

    /**
     * 保存在数据库中，用于stop任务需要
     */
    private String jobId;

    public SubmitResponse(){}

    public SubmitResponse(long taskId, String clusterId,int port, ResponseStatus responseStatus,String restUrl,String jobId) {
        this.taskId = taskId;
        this.jobId = jobId;
        this.port = port;
        this.clusterId = clusterId;
        this.responseStatus = responseStatus;
    }

}
