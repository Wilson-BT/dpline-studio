package com.handsome.console.service;

import com.handsome.common.enums.TaskType;
import com.handsome.dao.dto.FlinkTaskDefinitionDto;
import com.handsome.dao.entity.User;

import java.util.Map;

public interface FlinkTaskDefinitionService {


    Map<String, Object> createFlinkTaskDefinitionDraft(User loginUser, String taskName,
                                                              int flinkVersionId,
                                                              long projectCode,
                                                              TaskType taskType,
                                                              String description
                                                       );

    Map<String, Object> updateTaskDefinitionDraft(User loginUser,FlinkTaskDefinitionDto flinkTaskDefinitionDto);

    Map<String, Object> createFlinkTaskDefinitionTag(User loginUser, long flinkTaskDefinitionId,String flinkTaskTagName);

    Map<String, Object> deleteFlinkTaskDefinitionTag(User loginUser, long flinkTaskTagId);

    Map<String, Object> runLocalModeTask(User loginUser, long flinkTaskTagId, String socketId);

    Map<String, Object> updateTaskDefinitionRes(User loginUser,long taskDefinitionId,String resourceIds, String udfIds);
}
