package com.dpline.common.params;

import com.dpline.common.util.JSONUtils;
import lombok.Data;

@Data
@Deprecated
public class DataStreamConfig {

    private Long jarId;

    private Long mainResourceId;

    private String mainClass;

    private String appArgs;

    private String name;

    public String toJsonString(){
        return JSONUtils.toJsonString(this);
    }

}