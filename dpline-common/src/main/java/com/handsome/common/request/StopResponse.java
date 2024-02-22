package com.handsome.common.request;

import com.handsome.common.enums.ResponseStatus;
import lombok.Data;

public class StopResponse {

    /**
     * cluster id
     */
    String clusterId;

    /**
     * job Id
     */
    String jobId;

    /**
     * stop status
     */
    ResponseStatus responseStatus;

    /**
     * save point address
     */
    String savePointAddress;

    public StopResponse(String clusterId, String jobId, ResponseStatus responseStatus, String savePointAddress) {
        this.clusterId = clusterId;
        this.jobId = jobId;
        this.responseStatus = responseStatus;
        this.savePointAddress = savePointAddress;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getSavePointAddress() {
        return savePointAddress;
    }

    public void setSavePointAddress(String savePointAddress) {
        this.savePointAddress = savePointAddress;
    }

}
