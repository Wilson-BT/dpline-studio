package com.dpline.common.enums;

public class FlinkVersion {

    private int majorVersion;

    private int minorVersion;

    private String fullVersion;

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public void setFullVersion(String fullVersion) {
        this.fullVersion = fullVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }
}
