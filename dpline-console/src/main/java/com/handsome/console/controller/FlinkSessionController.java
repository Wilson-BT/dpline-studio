package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.console.exception.ApiException;
import com.handsome.console.service.FlinkSessionService;
import com.handsome.common.util.Result;
import com.handsome.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static com.handsome.common.enums.Status.*;

/**
 * auth follow by k8s_namespace
 */
@RestController
@RequestMapping("flink_session")
public class FlinkSessionController extends BaseController {

    @Autowired
    private FlinkSessionService flinkSessionService;

    /**
     * create task draft
     *
     * @param loginUser  login user
     * @return Result<Object>
     */
    @PostMapping()
    @ApiException(CREATE_FLINK_SESSION_CLUSTER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> createFlinkSession(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @RequestParam("flinkSessionName") String flinkSessionName,
                                             @RequestParam("k8sNamespaceId") long k8sNamespaceId,
                                             @RequestParam("taskmanagerNum") int taskmanagerNum,
                                             @RequestParam("taskmanagerMemSize") int taskmanagerMemSize,
                                             @RequestParam("taskmanagerCpuNum") int taskmanagerCpuNum,
                                             @RequestParam("taskmanagerSlotNum") int taskmanagerSlotNum,
                                             @RequestParam("jobmanagerProcessSize") int jobmanagerProcessSize,
                                             @RequestParam("kubernetesClusterId") String kubernetesClusterId
    ){
        return flinkSessionService.createFlinkSessionDefinition(loginUser, flinkSessionName,
                                                        k8sNamespaceId,
                                                        taskmanagerNum,
                                                        taskmanagerCpuNum,
                                                        taskmanagerMemSize,
                                                        taskmanagerSlotNum,
                                                        jobmanagerProcessSize,
                                                        kubernetesClusterId);
    }


    @PutMapping("{id}")
    @ApiException(CREATE_FLINK_SESSION_CLUSTER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> updateFlinkSession(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable("id") int id,
                                             @RequestParam("flinkSessionName") String flinkSessionName,
                                             @RequestParam("k8sNamespaceId") long k8sNamespaceId,
                                             @RequestParam("taskmanagerNum") int taskmanagerNum,
                                             @RequestParam("taskmanagerMemSize") int taskmanagerMemSize,
                                             @RequestParam("taskmanagerCpuNum") int taskmanagerCpuNum,
                                             @RequestParam("taskmanagerSlotNum") int taskmanagerSlotNum,
                                             @RequestParam("jobmanagerProcessSize") int jobmanagerProcessSize,
                                             @RequestParam("kubernetesClusterId") String kubernetesClusterId
    ){
        return flinkSessionService.updateFlinkSessionDefinition(loginUser,id,
                flinkSessionName,
                k8sNamespaceId,
                taskmanagerNum,
                taskmanagerCpuNum,
                taskmanagerMemSize,
                taskmanagerSlotNum,
                jobmanagerProcessSize,
                kubernetesClusterId);
    }


    @PostMapping("{id}/start")
    @ApiException(CREATE_FLINK_SESSION_CLUSTER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> startFlinkSession(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                             @PathVariable("id") int id
    ){
        return flinkSessionService.startFlinkSession(id);
    }

    @DeleteMapping("{id}")
    @ApiException(DELETE_FLINK_SESSION_CLUSTER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> deleteFlinkSession(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @PathVariable("id") int id

    ){
        return flinkSessionService.deleteFlinkSession(id);
    }

    @PostMapping("{id}/stop")
    @ApiException(START_FLINK_SESSION_CLUSTER_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = "loginUser")
    public Result<Object> stopFlinkSession(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                            @PathVariable("id") int id
    ){
        return flinkSessionService.stopFlinkSession(id);
    }


}
