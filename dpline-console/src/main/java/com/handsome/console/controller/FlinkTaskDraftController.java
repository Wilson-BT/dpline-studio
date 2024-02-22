package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.Status;
import com.handsome.common.enums.TaskType;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.dto.FlinkTaskDefinitionDto;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.FlinkTaskDefinitionService;
import com.handsome.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.handsome.common.enums.Status.*;

@RestController
@RequestMapping("flink_task_definition")
public class FlinkTaskDraftController extends BaseController {


    @Autowired
    FlinkTaskDefinitionService flinkTaskDefinitionService;

    /**
     * update task draft
     * @return
     */
    @PutMapping("{id}")
    @ApiException(UPDATE_TASK_DEFINITION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateTaskDefinitionAsDraft(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @Valid FlinkTaskDefinitionDto flinkTaskDefinitionDto, BindingResult result
                                                ){
        List<FieldError> fieldErrors = result.getFieldErrors();
        if(!fieldErrors.isEmpty()){
            return Result.errorWithArgs(Status.NULL_ERROR,fieldErrors.get(0).getDefaultMessage());
        }
        Map<String, Object> map = flinkTaskDefinitionService.updateTaskDefinitionDraft(loginUser, flinkTaskDefinitionDto);
        return returnDataList(map);
    }


    /**
     * update res_config
     *
     * @param loginUser
     * @param taskDefinitionId
     * @param resourceIds
     * @param udfIds
     * @return
     */
    @PutMapping("{id}/res_config")
    @ApiException(UPDATE_TASK_DEFINITION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateTaskDefinitionResource(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                      @PathVariable(value = "id") long taskDefinitionId,
                                                      @RequestParam(value = "resourceIds",defaultValue = "[]",required = false) String resourceIds,
                                                      @RequestParam(value = "udfIds",defaultValue = "[]",required = false)String udfIds
    ){
        Map<String, Object> map = flinkTaskDefinitionService.updateTaskDefinitionRes(loginUser, taskDefinitionId,resourceIds,udfIds);
        return returnDataList(map);
    }

    /**
     * create task draft
     *
     * @param loginUser  login user
     * @param taskName  task name
     * @param flinkVersionId flink version id
     * @param projectCode project code
     * @param taskType task type
     * @return Result<Object>
     */
    @PostMapping()
    @ApiException(CREATE_TASK_DEFINITION_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createTaskDefinitionDraft(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                    @RequestParam(value = "taskName") String taskName,
                                                    @RequestParam(value = "flinkVersionId") int flinkVersionId,
                                                    @RequestParam(value = "projectCode") long projectCode,
                                                    @RequestParam(value = "taskType") TaskType taskType,
                                                    @RequestParam(value = "description",required = false) String description
    ){
        Map<String, Object> flinkTaskDefinitionDraftResult = flinkTaskDefinitionService.createFlinkTaskDefinitionDraft(loginUser, taskName, flinkVersionId, projectCode, taskType,description);
        return returnDataList(flinkTaskDefinitionDraftResult);
    }

    @PostMapping("{id}/tag")
    @ApiException(CREATE_TASK_TAG_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createTaskDefinitionTag(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                  @PathVariable("id") long flinkTaskId,
                                                  @RequestParam("tagName") String flinkTaskTagName
    ){
        Map<String, Object> flinkTaskDefinitionDraftResult = flinkTaskDefinitionService.createFlinkTaskDefinitionTag(loginUser, flinkTaskId,flinkTaskTagName);
        return returnDataList(flinkTaskDefinitionDraftResult);
    }



    @DeleteMapping("{id}/tag")
    @ApiException(DELETE_TASK_TAG_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deleteTaskDefinitionTag(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                  @PathVariable("id") long flinkTaskTagId
    ){
        // if this tag task is created instance we can not delete this tag，real-time-stream could only create one instance on one tag.
        Map<String, Object> flinkTaskDefinitionDraftResult = flinkTaskDefinitionService.deleteFlinkTaskDefinitionTag(loginUser, flinkTaskTagId);
        return returnDataList(flinkTaskDefinitionDraftResult);
    }
    @PostMapping("{id}/debug")
//    @ApiException()
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> debugTaskDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @PathVariable("id") long flinkTaskTagId,
                                              @RequestParam("sid") String socketId
    ){
        // 直接开始运行
        Map<String,Object> resultMap = flinkTaskDefinitionService.runLocalModeTask(loginUser,flinkTaskTagId,socketId);
        return returnDataList(resultMap);
    }

    /**
     * 校验 flink task sql
     *
     * TODO
     * @param loginUser
     * @param flinkTaskTagId
     * @param socketId
     * @return
     */
    @PostMapping("{id}/verifySql")
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> verifyTaskSqlDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @PathVariable("id") long flinkTaskTagId,
                                              @RequestParam("sid") String socketId
    ){
        // 直接开始运行
//         Map<String,Object> resultMap = flinkTaskDefinitionService.runLocalModeTask(loginUser,flinkTaskTagId,socketId);
        return returnDataList(null);
    }
    /**
     * 获取执行计划 / 血缘关系
     *
     * TODO
     * @param loginUser
     * @param flinkTaskTagId
     * @param socketId
     * @return
     */
    @GetMapping("{id}/exec_plain")
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> getBloodRelation(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                  @PathVariable("id") long flinkTaskTagId,
                                                  @RequestParam("sid") String socketId
    ){
        // 直接开始运行
//        Map<String,Object> resultMap = flinkTaskDefinitionService.runLocalModeTask(loginUser,flinkTaskTagId,socketId);
        return returnDataList(null);
    }

    @PostMapping("{id}/stop")
    @ApiException(DELETE_TASK_TAG_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> stopDebugTaskDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                              @PathVariable("id") long flinkTaskTagId
    ){
        // if this tag task is created instance we can not delete this tag，real-time-stream could only create one instance on one tag.
//        Map<String, Object> flinkTaskDefinitionDraftResult = flinkTaskDefinitionService.deleteFlinkTaskDefinitionTag(loginUser, flinkTaskTagId);
        return returnDataList(null);
    }

}