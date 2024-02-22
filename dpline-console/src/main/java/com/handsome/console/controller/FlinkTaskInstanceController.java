package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.*;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.FlinkTaskInstanceService;
import com.handsome.common.util.Result;
import com.handsome.dao.dto.FlinkTaskInstanceConfigDto;
import com.handsome.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


import javax.validation.Valid;

import static com.handsome.common.enums.Status.*;

@RestController
@RequestMapping("flink_task_instance")
public class FlinkTaskInstanceController extends BaseController {

    @Autowired
    FlinkTaskInstanceService flinkTaskInstanceService;


    /**
     * create task instance on one task tag
     *
     * @param loginUser
     * @param flinkTaskTagId
     * @return
     */
    @PostMapping()
    @ApiException(CREATE_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createTaskInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                  @RequestParam("tagId") long flinkTaskTagId,
                                                  @RequestParam("flinkInstanceName") String flinkInstanceName,
                                                  @RequestParam(value = "imageId",required = false) int imageId
    ){
        return flinkTaskInstanceService.createTaskInstance(loginUser,flinkTaskTagId,flinkInstanceName,imageId);
    }

    @PutMapping("{id}/config")
    @ApiException(UPDATE_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateTaskInstanceRunTimeConfig(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                          @RequestBody @Valid FlinkTaskInstanceConfigDto flinkTaskInstanceConfigDto
    ){

        return flinkTaskInstanceService.updateTaskInstanceRunConfig(loginUser,flinkTaskInstanceConfigDto);
    }

    /**
     *
     * compile and deploy on s3
     *
     * @param loginUser
     * @param taskInstanceId
     * @return
     */
    @PostMapping("{id}/deploy")
    @ApiException(DEPLOY_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deployTaskInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable("id") long taskInstanceId
    ){
        return flinkTaskInstanceService.deployTaskInstance(loginUser,taskInstanceId);
    }

    /**
     * 运行时生成 Pod 文件
     * @param loginUser
     * @param taskInstanceId
     * @param checkpointStartType
     * @param savePointId
     * @param manualSavePointAddress
     * @return
     */
    @PostMapping("{id}/run")
    @ApiException(RUN_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> runTaskInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                          @PathVariable("id") long taskInstanceId,
                                          @RequestParam(value = "checkPointType",required = false,defaultValue = "FROM_LAST_CHECKPOINT")
                                                      CheckpointStartType checkpointStartType,
                                          // 从指定的 savePoint的位置开始
                                          @RequestParam(value = "savePointId",required = false) Long savePointId,
                                          // 从指定 savepoint 目录 开始
                                          @RequestParam(value = "manualSavePointAddress",required = false) String manualSavePointAddress
                                          ){
        return returnDataList(flinkTaskInstanceService.runTaskInstance(loginUser,taskInstanceId,checkpointStartType,savePointId,manualSavePointAddress));
    }

    @PostMapping("{id}/stop")
    @ApiException(STOP_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> stopTaskInstance(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                          @PathVariable("id") long taskInstanceId,
                                          @RequestParam(value = "withSavePointAddress",required = false) Boolean withSavePointAddress,
                                          @RequestParam(value = "savePointAddress",required = false,defaultValue = "") String savePointAddress
    ){
        return returnDataList(flinkTaskInstanceService.stopTaskInstance(loginUser,taskInstanceId,withSavePointAddress,savePointAddress));
    }

    /**
     * get flink instance from
     *
     * @param loginUser
     * @return
     */
    @GetMapping("/list")
//    @ApiException(STOP_FLINK_TASK_INSTANCE_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> getTaskInstanceStatus(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser
    ){
        return flinkTaskInstanceService.listAllTaskInstanceStatus(loginUser);
    }



    /**
     * update flink task config
     *
     * @param loginUser
     * @return
     */
//    @PostMapping("{id}/run_config")
//    @ApiException(UPDATE_FLINK_TASK_INSTANCE_ERROR)
//    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
//    public Result<Object> updateTaskInstanceRunTimeConfig( @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
//                                                          @PathVariable("id") long flinkTaskInstanceId,
//                                                          @RequestParam("runMode")RunModeType runMode,
//                                                          @RequestParam(value = "openChain",required = false) Flag openChain,
//                                                          @RequestParam("resourceOptions") ResourceOptions resourceOptions,
//                                                          @RequestParam(value = "resolveOrder",required = false) Flag resolveOrder,
//                                                          @RequestParam(value = "checkpointOptions",required = false) CheckpointOptions checkpointOptions,
//                                                          @RequestParam(value = "k8sNamespaceId",required = false) int k8sNamespaceId,
//                                                          @RequestParam(value = "k8sSessionClusterId",required = false) int k8sSessionClusterId,
//                                                          @RequestParam(value = "exposedType",required = false) ExposedType exposedType,
//                                                          @RequestParam(value = "warningType",required = false) Flag warningType,
//                                                          @RequestParam(value = "warningGroupId",required = false) int warningGroupId,
//                                                          @RequestParam(value = "restartNum",required = false) int restartNum,
//                                                          @RequestParam(value = "restartOptions",required = false) RestartOptions restartOptions
//                                                          ){
//        flinkTaskInstanceService.updateTaskInstanceRunConfig(flinkTaskInstanceId,runMode,
//                openChain,resourceOptions,resolveOrder,checkpointOptions,k8sNamespaceId,k8sSessionClusterId,
//                exposedType,warningType,warningGroupId,restartNum,restartOptions);
//        return null;
//    }


}
