package com.handsome.common.request;

import com.handsome.common.enums.RunModeType;
import lombok.Data;

@Data
public class TriggerRequest extends RemoteRequest{


    long taskId;
    /**
     * cluster id, 做路径
     */
    String clusterId;

    /**
     * 数据库的jobId -> taskId，做hash
     */
    String jobId;

    /**
     * run mode
     */
    RunModeType runModeType;

    /**
     * if with savepoint address
     */
    Boolean withSavePointAddress;

    /**
     * save point address
     */
    String savePointAddress;

}
