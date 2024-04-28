package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.JobServiceImpl;
import com.dpline.dao.rto.JobRto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    JobServiceImpl jobServiceImpl;

    @ApiOperation(value = "查询任务列表")
    @RequestMapping(value = "/queryJob", method = RequestMethod.POST)
    public Result<Object> queryJob(@RequestBody JobRto jobRto,@RequestHeader Long projectId) {
        jobRto.setProjectId(projectId);
        return jobServiceImpl.listJob(jobRto);
    }

    @ApiOperation(value = "查询更新用户")
    @RequestMapping(value = "/selectUpdateUser", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> selectUpdateUser(@RequestHeader Long projectId) {
        Result<Object> result = new Result<>();
        return result;
    }

    @ApiOperation(value = "任务部署")
    @RequestMapping(value = "/deploy", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deployJob(@RequestBody JobRto jobRto) {
        return jobServiceImpl.deployJob(jobRto);
    }

    @ApiOperation(value = "下线任务")
    @RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteJob(@RequestBody JobRto jobRto){
        return jobServiceImpl.deleteJob(jobRto);
    }

    @ApiOperation(value = "启动任务")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> startJob(@RequestBody JobRto jobRto){
        return jobServiceImpl.startJob(jobRto);
    }

    @ApiOperation(value = "停止任务")
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> stopJob(@RequestBody JobRto jobRto){
        return jobServiceImpl.stopJob(jobRto);
    }

    @RequestMapping(value = "/triggerSavepoint",method = RequestMethod.POST)
    @ApiOperation(value = "触发Savepoint")
    @AccessLogAnnotation
    public Result<Object> savepoint(@RequestBody JobRto jobRto){
        return jobServiceImpl.triggerSavepoint(jobRto);
    }


    @RequestMapping(value = "/updateRunConfig",method = RequestMethod.POST)
    @ApiOperation(value = "更新运行配置")
    @AccessLogAnnotation
    public Result<Object> updateConfig(@RequestParam(value = "runtimeOptions") String runtimeOptions,
                                       @RequestParam("motorVersionId") long motorVersionId,
                                       @RequestParam(value = "appArgs",required = false) String appArgs,
                                       @RequestParam(value = "otherRuntimeConfig",required = false) String otherRuntimeConfig,
                                       @RequestParam(value = "clusterId") long clusterId,
                                       @RequestParam(value = "fileType") String fileType,
                                       @RequestParam(value = "imageId",required = false) long imageId,
                                       @RequestParam(value = "id") long jobId,
                                       @RequestParam(value = "runModeType") String runModeType,
                                       @RequestParam(value = "clusterType") String clusterType


    ){
        return jobServiceImpl.updateRunTimeConfig(
                                 runtimeOptions,
                                 motorVersionId,
                                 appArgs,
                                 otherRuntimeConfig,
                                 clusterId,
                                 fileType,
                                 imageId,
                                 jobId,
                                 runModeType,
                                 clusterType);
    }

    @RequestMapping(value = "/updateAlertConfig",method = RequestMethod.POST)
    @ApiOperation(value = "更新运行配置")
    @AccessLogAnnotation
    public Result<Object> updateAlert(@RequestParam("id") Long jobId,
                                      @RequestParam(value = "alertMode") String alertMode,
                                      @RequestParam("alertInstanceId") long alertInstanceId
    ){
        return jobServiceImpl.updateAlertConfig(jobId,alertMode,alertInstanceId);
    }


    @RequestMapping(value = "/jobConf",method = RequestMethod.POST)
    @ApiOperation(value = "更新运行配置")
    @AccessLogAnnotation
    public Result<Object> jobConf(@RequestParam("id") Long jobId,
                                  @RequestParam("confType") String confType
    ){
        return jobServiceImpl.getJobConf(jobId,confType);
    }




}
