package com.handsome.common.request;


import com.handsome.common.enums.RunModeType;
import lombok.Data;

@Data
public class StopRequest extends RemoteRequest {


    long taskId;
    /**
     * cluster id
     */
    String clusterId;

//    /**
//     * pipeline task name，如果是appliction 模式，就是 clusterId，
//     *                    如果是session 模式，使用 taskname
//     */
//    String taskName;

    /**
     * 数据库的jobId
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

    /**
     * name space
     */
    String nameSpace;

    /**
     * kube path
     */
    String kubePath;

}
