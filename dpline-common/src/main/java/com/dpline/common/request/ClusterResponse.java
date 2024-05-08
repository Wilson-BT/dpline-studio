package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class ClusterResponse extends Response {

    public ClusterResponse() {
    }

    public ClusterResponse(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

}
