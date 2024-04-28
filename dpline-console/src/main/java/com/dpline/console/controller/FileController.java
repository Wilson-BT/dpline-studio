package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.FileServiceImpl;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.dto.FileDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(tags="文件管理")
@RequestMapping(value = "/file")
@Slf4j
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @ApiOperation(value="获取用户历史作业列表")
    @RequestMapping(value="/fileHistory",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> fileHistory(){
        Result<Object> data = new Result<>();
        data.ok();
        data.setData(fileService.fileHistory(ContextUtils.get().getUser()));
        return data;
    }

    @ApiOperation(value="查询基础信息")
    @RequestMapping(value="/queryBaseInfo",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> queryBaseInfo(@RequestBody FileDto fileBO) {
        return fileService.queryBaseInfo(fileBO.getId());
    }

    @ApiOperation(value="修改基础信息")
    @RequestMapping(value="/updateBaseInfo",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateBaseInfo(@RequestBody FileDto fileDto) {
        return fileService.updateBaseInfo(fileDto);
    }


    @ApiOperation(value="是否允许新增作业")
    @RequestMapping(value="/allowAddFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> allowAddJob(@RequestHeader(value="projectId") Long projectId){
        Result<Object> data = new Result<>();
        data.ok();
        data.setData(fileService.allowAddJob(projectId));
        return data;
    }
    @ApiOperation(value="是否允许新增作业")
    @RequestMapping(value="/allowEditFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> allowEditFile(@RequestBody FileDto fileDto){
        return fileService.allowEditFile(fileDto);
    }


    @ApiOperation(value="查询作业")
    @RequestMapping(value="/queryFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> queryFile(@RequestBody FileDto fileDto, @RequestHeader(value="projectId") Long projectId){
        fileDto.setProjectId(projectId);
        return fileService.queryFile(fileDto);

    }

    @ApiOperation(value="添加作业")
    @RequestMapping(value="/addFile",method= RequestMethod.POST)
    public Result<Object> addFile(@RequestBody FileDto fileDto){
        return fileService.addFile(fileDto);
    }


    //进行文件编辑锁定
    @ApiOperation(value="作业详情[编辑]")
    @RequestMapping(value="/detailFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> detailFile(@RequestBody FileDto fileDto){
        return fileService.getDetailFile(fileDto);
    }


    @ApiOperation(value="删除作业")
    @RequestMapping(value="/deleteFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteFile(@RequestBody FileDto fileDto){
        return fileService.deleteFile(fileDto);
    }

    @ApiOperation(value="修改作业")
    @RequestMapping(value="/updateFile",method= RequestMethod.POST)
    public Result<Object> updateFile(@RequestBody FileDto fileDto){
        return fileService.updateFile(fileDto);
    }

    @ApiOperation(value="获取文件状态")
    @RequestMapping(value="/checkState",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> checkState(@RequestBody List<Long> fileIds){
        return fileService.checkState(fileIds);
    }


    @ApiOperation(value="项目获取引擎")
    @RequestMapping(value="/getFileClusters",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> getFileClusters(@RequestParam("clusterType") String clusterType){
        return fileService.getAuthedClusters(clusterType);
    }


    @ApiOperation(value = "获取任务DAG")
    @RequestMapping(value="/getFileDAG",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> fileDag(@RequestParam("fileId") long fileId){
        return fileService.getFileDag(fileId);
    }


    @ApiOperation(value = "获取任务DAG")
    @RequestMapping(value="/removeFile",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> removeFile(@RequestParam("fileId") long fileId,
                                     @RequestParam("folderId") long folderId){
        return fileService.removeFile(fileId,folderId);
    }

    @ApiOperation(value = "获取任务DAG")
    @RequestMapping(value="/online",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> online(@RequestParam(value = "fileId") Long fileId,
                                 @RequestParam(value = "jobName") String jobName,
                                 @RequestParam(value = "remark") String remark,
                                 @RequestHeader(value = "projectId") Long projectId
                                 ){
        return fileService.onlineFile(fileId,jobName,remark,projectId);
    }


}