package com.dpline.flink.core.stop;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class SavePointInfo {

    private static final String FIELD_NAME_LOCATION = "location";

    private static final String FIELD_NAME_FAILURE_CAUSE = "failure-cause";

    @JsonProperty(FIELD_NAME_LOCATION)
    @Nullable
    private final String location;

    @JsonProperty(FIELD_NAME_FAILURE_CAUSE)
    @Nullable
    private final String failureCause;

    @JsonCreator
    public SavePointInfo(@JsonProperty(FIELD_NAME_LOCATION) @Nullable final String location,
                         @JsonProperty(FIELD_NAME_FAILURE_CAUSE) @Nullable String failureCause){
        this.location = location;
        this.failureCause = failureCause;
    }

}
