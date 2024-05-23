package com.dpline.operator.entity;

import com.dpline.common.enums.RunModeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cluster
 */
@Data
@Builder
public class ClusterEntity {

    /**
     * ClusterEntity id
     */
    private Long id;

    /**
     * application id
     */
    private Map<String, ApplicationEntity> applicationIdMap;

    public ClusterEntity(Long id) {
        applicationIdMap = new ConcurrentHashMap<>();
        this.id = id;
    }

    public void addNewJob(ApplicationEntity clusterEntity){
        if(applicationIdMap.containsKey(clusterEntity.getApplicationId())){
            return;
        }
        applicationIdMap.put(clusterEntity.getApplicationId(),clusterEntity);
    }

    public void addNewJob(String applicationId, RunModeType runModeType,String runJobId, String restUrl, JobTask jobTask){
        // session mode, create session mode,should add into cluster
        if(RunModeType.YARN_SESSION.equals(runModeType) && !applicationIdMap.containsKey(applicationId)){
            return;
        }

        if(applicationIdMap.containsKey(applicationId)){
            ApplicationEntity applicationSessionEntity = applicationIdMap.get(applicationId);
            applicationSessionEntity.addNewJob(runJobId,jobTask);
            return;
        }
        ApplicationEntity epplicationEntity = ApplicationEntity.builder()
                .applicationId(applicationId)
                .runModeType(runModeType)
                .restUrl(restUrl)
                .build();
        epplicationEntity.addNewJob(runJobId,jobTask);
        applicationIdMap.put(applicationId,epplicationEntity);
    }


    public ApplicationEntity getApplicationEntity(String applicationId){
        return applicationIdMap.get(applicationId);
    }

    public void removeApplicationEntity(String applicationId) {
        this.applicationIdMap.remove(applicationId);
    }
}
