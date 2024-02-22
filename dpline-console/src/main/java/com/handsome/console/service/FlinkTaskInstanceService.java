package com.handsome.console.service;

import com.handsome.common.enums.CheckpointStartType;
import com.handsome.common.util.Result;
import com.handsome.dao.dto.FlinkTaskInstanceConfigDto;
import com.handsome.dao.entity.User;

import java.util.Map;

public interface FlinkTaskInstanceService {
    // create flink instance on flink task tag
    Result<Object> createTaskInstance(User loginUser,long flinkTaskTagId, String flinkInstanceName,int imageId);


//    Result<Object> updateTaskInstanceRunConfig(long flinkTaskInstanceId, RunModeType runMode,
//                                     Flag openChain, ResourceOptions resourceOptions,
//                                     Flag resolveOrder, CheckpointOptions checkpointOptions,
//                                     int k8sNamespaceId, int k8sSessionClusterId,
//                                     ExposedType exposedType, Flag warningType,
//                                     int warningGroupId, int restartNum,
//                                     RestartOptions restartOptions);
    Result<Object> updateTaskInstanceRunConfig(User loginUser,FlinkTaskInstanceConfigDto flinkRunTaskInstance);

    Result<Object> deployTaskInstance(User loginUser,long taskInstanceId);

    Map<String, Object> runTaskInstance(User loginUser, long taskInstanceId, CheckpointStartType checkpointStartType, Long savePointId, String savePointAddress);

    Map<String,Object> stopTaskInstance(User loginUser, long taskInstanceId,boolean withSavePointAddress, String savePointAddress);

    Map<String,Object> triggerSavePoint(User loginUser, long taskInstanceId,boolean withSavePointAddress, String savePointAddress);

    Result<Object> listAllTaskInstanceStatus(User loginUser);
}
