package com.handsome.operator.entry;

import lombok.Data;

@Data
public class RunningCacheTask {

    private Long taskId;

    private String clusterId;

    private Integer port;

    private String nameSpace;

    private String kubePath;

}
