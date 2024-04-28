package com.dpline.common.enums;

public enum ClusterType {

    YARN("yarn"),
    KUBERNETES("kubernetes"),
    LOCAL("local"),
    REMOTE("remote");

    private String value;

    ClusterType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ClusterType of(String value){
        for(ClusterType clusterType : ClusterType.values()){
            if(clusterType.getValue().equals(value)){
                return clusterType;
            }
        }
        return null;
    }
}
