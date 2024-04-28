package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.SavePointServiceImpl;
import com.dpline.dao.rto.JobSavepointRto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 检查点位置
 *
 */
@RestController
@RequestMapping("/savepoint")
public class SavepointController {

    @Autowired
    SavePointServiceImpl savePointServiceImpl;

    @PostMapping("/queryCheckpointAndSavepoint")
    @AccessLogAnnotation
    public Result<Object> queryCheckpointAndSavepoint(@RequestBody JobSavepointRto jobSavepointRto){
        return savePointServiceImpl.queryCheckpointAndSavepoint(jobSavepointRto);
    }


    @PostMapping("/queryAllSavePoint")
    @AccessLogAnnotation
    public Result<Object> queryAllSavePoint(@RequestParam("jobId") Long jobId){
        return savePointServiceImpl.queryAllSavePoint(jobId);
    }

    @PostMapping("/deleteSavePoint")
    @AccessLogAnnotation
    public Result<Object> deleteSavePoint(@RequestParam("id") Long savepointId){
        return savePointServiceImpl.deleteSavePoint(savepointId);
    }

    @PostMapping("/deleteCheckPoint")
    @AccessLogAnnotation
    public Result<Object> deleteCheckpoint(@RequestParam("checkpointPath") String checkpointPath){
        return savePointServiceImpl.deleteCheckPoint(checkpointPath);
    }


}
