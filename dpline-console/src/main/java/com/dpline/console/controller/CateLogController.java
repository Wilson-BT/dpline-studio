package com.dpline.console.controller;


import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cateLog")
public class CateLogController {


    @ApiOperation(value = "获取catelog表")
    @RequestMapping("/query")
    @AccessLogAnnotation
    public Result<Object> queryCateLog(){
        return null;
    }



}
