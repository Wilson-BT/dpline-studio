package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

/**
 *
 */
@Data
public class TaskAlertEditResponse extends Response {

    public TaskAlertEditResponse() {
    }

    public TaskAlertEditResponse(ResponseStatus status,String msg) {
        this.responseStatus = status;
    }
}
