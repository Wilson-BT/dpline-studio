package com.dpline.flink.core.stop;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

@Data
public class QueueStatus implements Serializable {

    private static final String FIELD_NAME_ID = "id";

    @JsonProperty(value = FIELD_NAME_ID, required = true)
    private final Id id;

    @JsonCreator
    public QueueStatus(@JsonProperty(value = FIELD_NAME_ID, required = true) final Id id) {
        this.id = requireNonNull(id, "statusId must not be null");
    }

    /** Defines queue statuses. */
    public enum Id {
        IN_PROGRESS,
        COMPLETED
    }
}
