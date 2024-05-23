package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;


@Data
public class SubmitResponse extends Response {

    private String applicationId;

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

    public SubmitResponse(long jobId, String applicationId,int port, ResponseStatus responseStatus,String runJobId) {
        this.jobId = jobId;
        this.runJobId = runJobId;
        this.port = port;
        this.applicationId = applicationId;
        this.responseStatus = responseStatus;
        this.restUrl = "";
    }

    public SubmitResponse(long jobId, String applicationId,int port, ResponseStatus responseStatus,String runJobId,String restUrl){
        this.jobId = jobId;
        this.runJobId = runJobId;
        this.port = port;
        this.applicationId = applicationId;
        this.responseStatus = responseStatus;
        this.restUrl = restUrl;
    }


    @Override
    public String toString() {
        return "SubmitResponse{" +
            "applicationId='" + applicationId + '\'' +
            ", port=" + port +
            ", jobId=" + jobId +
            ", restUrl='" + restUrl + '\'' +
            ", responseStatus=" + responseStatus +
            ", runJobId='" + runJobId + '\'' +
            '}';
    }
}
