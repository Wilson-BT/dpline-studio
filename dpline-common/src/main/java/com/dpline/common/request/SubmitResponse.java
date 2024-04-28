package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;


@Data
public class SubmitResponse extends Response {

    private String clusterId;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * real jobId
     */
    private Long jobId;

    /**
     * real Rest Url
     */
    private String restUrl;


    /**
     * real run job id
     */
    private String runJobId;

    public SubmitResponse(){
    }

    public SubmitResponse(long jobId, String clusterId,int port, ResponseStatus responseStatus,String runJobId) {
        this.jobId = jobId;
        this.runJobId = runJobId;
        this.port = port;
        this.clusterId = clusterId;
        this.responseStatus = responseStatus;
    }


    @Override
    public String toString() {
        return "SubmitResponse{" +
            "clusterId='" + clusterId + '\'' +
            ", port=" + port +
            ", jobId=" + jobId +
            ", restUrl='" + restUrl + '\'' +
            ", responseStatus=" + responseStatus +
            ", runJobId='" + runJobId + '\'' +
            '}';
    }
}
