package com.handsome.operator.entry;

import lombok.Data;

@Data
public class RunningCacheCluster {

    private String clusterId;

    private Integer port;

    private String nameSpace;

    private String kubePath;


    public RunningCacheCluster(String clusterId, Integer port, String nameSpace, String kubePath) {
        this.clusterId = clusterId;
        this.port = port;
        this.nameSpace = nameSpace;
        this.kubePath = kubePath;
    }

}
