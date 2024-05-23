package com.dpline.operator.entity;

import com.dpline.common.enums.AlertMode;
import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.util.Asserts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application or Session
 */
@Data
@Builder
@AllArgsConstructor
public class ApplicationEntity {

    /**
     * application mode then application id
     * session mode then cluster id
     */
    private String applicationId;

    /**
     * run mode type
     */
    private RunModeType runModeType;


    /**
     * rest url
     */
    private String restUrl;


    /**
     * runJobId jobTask
     */
    private Map<String, JobTask> jobTaskMap = new ConcurrentHashMap<>();

    public ApplicationEntity(String applicationId,RunModeType runModeType,String restUrl){
        this.applicationId = applicationId;
        this.runModeType = runModeType;
    }


    public void addNewJob(String runJobId, JobTask jobTask) {
        if (Asserts.isNull(jobTaskMap)){
            jobTaskMap = new ConcurrentHashMap<>();
        }
        this.jobTaskMap.put(runJobId, jobTask);
    }

    public void removeJob(String jobId){
        this.jobTaskMap.remove(jobId);
    }
}
