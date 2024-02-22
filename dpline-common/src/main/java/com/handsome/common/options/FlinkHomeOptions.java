package com.handsome.common.options;

public class FlinkHomeOptions {

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;


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