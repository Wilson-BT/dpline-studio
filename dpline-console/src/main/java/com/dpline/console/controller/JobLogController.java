package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.DplineJobOperateLogImpl;
import com.dpline.dao.rto.DplineJobOperateLogRto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/log")
public class JobLogController {

    @Autowired
    DplineJobOperateLogImpl logServiceImpl;

    /**
     * 需要根据操作日志
     * @param jobId
     * @return
     */
    @PostMapping(value = "/logViewFromTime")
    @ApiOperation(value = "从某个时间戳开始查看日志")
    @AccessLogAnnotation
    public Result<Object> logViewFromTime(@RequestParam("id") long jobId,
                                          @RequestParam(value = "latestTime",required = false) String lastTime,
                                          @RequestParam(value = "limitCount",required = false, defaultValue = "1000") Integer limitCount
    ){
        return logServiceImpl.logViewFromTimeStamp(jobId, lastTime,limitCount);
    }

    @PostMapping(value = "/logTail")
    @ApiOperation(value = "Tail查看日志")
    @AccessLogAnnotation
    public Result<Object> logViewTail(@RequestParam("id") long jobId,
                                      @RequestParam(value = "limitCount",required = false,defaultValue = "1000") Integer limitCount){
        return logServiceImpl.logViewTail(jobId, limitCount);
    }

    /**
     * 需要根据操作日志
     * @param jobId
     * @return
     */
    @PostMapping(value = "/logViewToTime")
    @ApiOperation(value = "从某个时间戳开始查看日志")
    @AccessLogAnnotation
    public Result<Object> logViewToTime(  @RequestParam("id") long jobId,
                                          @RequestParam(value = "endTime",required = false) String endTime,
                                          @RequestParam(value = "limitCount",required = false, defaultValue = "1000") Integer limitCount
    ){
        return logServiceImpl.logViewToTime(jobId, endTime,limitCount);
    }

    /**
     * 查看操作日志
     * @return
     */
    @PostMapping(value = "/searchOperateLog")
    @ApiOperation(value = "查看操作日志")
    @AccessLogAnnotation
    public Result<Object> searchOperateLog(@RequestBody DplineJobOperateLogRto dplineJobOperateLogRto){
        return logServiceImpl.searchOperateLogByJobId(dplineJobOperateLogRto);
    }

    /**
     * 删除操作日志
     * @return
     */
    @PostMapping(value = "/delete")
    @ApiOperation(value = "删除日志")
    @AccessLogAnnotation
    public Result<Object> delete(@RequestParam("id") Long id,
                                 @RequestParam("operateLogPath") String operateLogPath){
        return logServiceImpl.deleteLog(id,operateLogPath);
    }

}
