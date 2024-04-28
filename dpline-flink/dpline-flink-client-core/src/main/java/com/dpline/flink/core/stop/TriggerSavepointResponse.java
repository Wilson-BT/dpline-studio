package com.dpline.flink.core.stop;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TriggerSavepointResponse implements Serializable {

    private static final String FIELD_NAME_REQUEST_ID = "request-id";

    @JsonProperty(FIELD_NAME_REQUEST_ID)
    private final String triggerId;

    @JsonCreator
    public TriggerSavepointResponse(@JsonProperty(FIELD_NAME_REQUEST_ID) String triggerId) {
        this.triggerId = triggerId;
    }

    public String getTriggerId() {
        return triggerId;
    }
}
