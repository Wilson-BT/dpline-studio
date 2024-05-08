package com.dpline.common.enums;

public enum ClusterType {

    /**
     * yarn
     */
    YARN("yarn"),

    /**
     * kubernetes
     */
    KUBERNETES("kubernetes"),

    /**
     * local
     */
    LOCAL("local"),

    /**
     * remote 本地模式
     */
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
