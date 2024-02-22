package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.FileTagServiceImpl;
import com.dpline.dao.entity.FileTag;
import com.dpline.dao.rto.FileTagRto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file/tag/")
public class FileTagController {

    @Autowired
    FileTagServiceImpl fileTagServiceImpl;

    @ApiOperation(value = "版本对比")
    @RequestMapping(value = "/compare",method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> compareTag(@RequestBody FileTagRto fileTagRto){
        // 版本ID
        return fileTagServiceImpl.compareCurrentAndTagFile(fileTagRto);
    }

    @ApiOperation(value="获取tag列表")
    @RequestMapping(value="/list",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> listTags(@RequestBody FileTagRto fileTagRto){
        return fileTagServiceImpl.getTags(fileTagRto);
    }

    @ApiOperation(value="删除tag")
    @RequestMapping(value="/deleteTag",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteTag(@RequestBody FileTag fileTag){
        return fileTagServiceImpl.deleteTag(fileTag);
    }

    @ApiOperation(value="添加tag")
    @RequestMapping(value="/addTag",method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> addTag(@RequestBody FileTagRto fileTagRto){
        return fileTagServiceImpl.addTag(fileTagRto);
    }

    @ApiOperation(value="添加tag")
    @RequestMapping(value="/rollback",method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> rollback(@RequestBody FileTagRto fileTagRto){
        return fileTagServiceImpl.rollback(fileTagRto);
    }

    @ApiOperation(value="添加tag")
    @RequestMapping(value="/online",method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> online(@RequestBody FileTagRto fileTagRto,
                                 @RequestHeader Long projectId){
        return fileTagServiceImpl.online(fileTagRto,projectId);
    }


}
