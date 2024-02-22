package com.dpline.common.request;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class StopResponse extends Response {

    /**
     * save point address
     */
    String savePointAddress;

    public StopResponse(ResponseStatus responseStatus, String savePointAddress) {
        this.responseStatus = responseStatus;
        this.savePointAddress = savePointAddress;
    }

    public StopResponse() {
    }
}
