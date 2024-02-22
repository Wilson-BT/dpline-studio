package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class FlinkDagResponse extends Response {

    String dagText;

    public FlinkDagResponse(String dagText, ResponseStatus responseStatus,String msg) {
        this.dagText = dagText;
        this.responseStatus = responseStatus;
        this.msg = msg;
    }

    public FlinkDagResponse() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        ResponseStatus responseStatus;

        String msg;

        String dagText;


        public Builder responseStatus(ResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder dagText(String dagText) {
            this.dagText = dagText;
            return this;
        }

        public FlinkDagResponse build() {
            return new FlinkDagResponse(dagText, responseStatus, msg);
        }
    }

}
