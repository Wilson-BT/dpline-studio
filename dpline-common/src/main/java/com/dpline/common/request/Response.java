package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {

    ResponseStatus responseStatus;

    String msg;

    public Response fail(){
        this.responseStatus = ResponseStatus.FAIL;
        return this;
    }
    public Response success(){
        this.responseStatus = ResponseStatus.SUCCESS;
        return this;
    }
    public Response fail(String msg){
        this.responseStatus = ResponseStatus.FAIL;
        this.msg = msg;
        return this;
    }
}
