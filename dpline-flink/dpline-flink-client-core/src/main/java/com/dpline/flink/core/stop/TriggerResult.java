package com.dpline.flink.core.stop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Nullable;
import java.io.Serializable;

@Data
public class TriggerResult implements Serializable {

    private static final String FIELD_NAME_STATUS = "status";

    public static final String FIELD_NAME_OPERATION = "operation";

    @JsonProperty(FIELD_NAME_STATUS)
    private final QueueStatus queueStatus;

    @JsonProperty(FIELD_NAME_OPERATION)
    @Nullable
    private final SavePointInfo value;

    @JsonCreator
    public TriggerResult (
        @JsonProperty(FIELD_NAME_STATUS) QueueStatus queueStatus,
        @JsonProperty(FIELD_NAME_OPERATION) @Nullable SavePointInfo value) {
        this.queueStatus = queueStatus;
        this.value = value;
    }
}
