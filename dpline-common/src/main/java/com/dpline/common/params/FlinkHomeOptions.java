package com.dpline.common.params;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class FlinkHomeOptions implements Serializable {

    /**
     * flink home path
     */
    private String flinkPath;
    /**
     * real version
     */
    private String realVersion;


    public String getRealVersion() {
        return realVersion;
    }

    public void setRealVersion(String realVersion) {
        this.realVersion = realVersion;
    }

    public FlinkHomeOptions() {
    }

    public FlinkHomeOptions(String flinkPath,String realVersion) {
        this.flinkPath = flinkPath;
        this.realVersion = realVersion;
    }

    public String getFlinkPath() {
        return flinkPath;
    }

    public void setFlinkPath(String flinkPath) {
        this.flinkPath = flinkPath;
    }

}