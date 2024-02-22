package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class K8sClusterResponse extends Response {

    public K8sClusterResponse() {
    }

    public K8sClusterResponse(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

}
