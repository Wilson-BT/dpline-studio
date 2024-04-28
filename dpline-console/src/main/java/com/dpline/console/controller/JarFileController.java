package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.JarFileServiceImpl;
import com.dpline.console.service.impl.MainResourceFileServiceImpl;
import com.dpline.dao.dto.JarFileDto;
import com.dpline.dao.dto.MainResourceRto;
import com.dpline.dao.entity.MainResourceFile;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/jar")
public class JarFileController {
    // 1.udf jar
    // 2.connector jar
    // 3.extended jar

    @Autowired
    JarFileServiceImpl jarFileServiceImpl;

    @Autowired
    MainResourceFileServiceImpl mainResourceFileServiceImpl;

    @ApiOperation("创建新资源")
    @RequestMapping(value = "/createMainSource", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> createMainFile(@RequestBody MainResourceRto mainResourceRto){
        return mainResourceFileServiceImpl.createNewMainFile(mainResourceRto);
    }

    @ApiOperation("查询资源列表")
    @RequestMapping(value = "/listMainResource", method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> listMainResource(@RequestBody MainResourceRto mainResourceDto){
        return mainResourceFileServiceImpl.listMainResource(mainResourceDto);
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = "/updateMainResource", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateMainFile(@RequestBody MainResourceRto mainResourceRto){
        return mainResourceFileServiceImpl.updateMainFile(mainResourceRto);
    }

    @ApiOperation(value="删除全部jar")
    @RequestMapping(value="/deleteWholeResource",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteWholeResource(@RequestBody MainResourceRto mainResourceDto){
        return mainResourceFileServiceImpl.deleteWholeResource(mainResourceDto);
    }

    @ApiOperation("搜索jar包")
    @RequestMapping(value = "/listJar", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> listJar(@RequestBody JarFileDto jarFileDto){
        return jarFileServiceImpl.listJar(jarFileDto);
    }


    @ApiOperation("更新jar的描述信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateJar(@RequestBody JarFileDto jarFileDto){
        return jarFileServiceImpl.updateJar(jarFileDto);
    }


    @ApiOperation(value="删除jar")
    @RequestMapping(value="/deleteJar",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteJar(@RequestBody JarFileDto jarFileDto){
        return jarFileServiceImpl.deleteJar(jarFileDto);
    }

    @ApiOperation("搜索jar包")
    @RequestMapping(value = "/searchJar", method = RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> searchJar(@RequestBody MainResourceRto mainResourceRto){
        return mainResourceFileServiceImpl.searchSource(mainResourceRto);
    }


//    @ApiOperation("查询jar包数量以及应用job计数")
//    @RequestMapping(value = "/referenceJobs", method = RequestMethod.POST)
//    public Result<Object> referenceJobs(@RequestBody JarFileDto jarFileDto){
//        return jarFileServiceImpl.queryJarReferenceJobs(jarFileDto);
//    }


    @ApiOperation(value="新增jar")
    @RequestMapping(value="/addJar",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> addJar(@RequestParam("file") MultipartFile file,
                                 @RequestHeader(value = "motorVersionId") Long motorVersionId,
                                 @RequestHeader(value = "jarName") String jarName,
                                 @RequestHeader(value = "description") String description,
                                 @RequestHeader(value = "fileMd5") String fileMd5,
                                 @RequestHeader(value = "mainResourceId") Long mainResourceId
                               ){
        MainResourceFile mainResourceFile = mainResourceFileServiceImpl.getMapper().selectById(mainResourceId);
        return jarFileServiceImpl.addJar(file,jarName,fileMd5, description,
                motorVersionId,
            mainResourceFile.getJarAuthType(),
            mainResourceFile.getProjectId(),
            mainResourceId);
    }

    @ApiOperation(value="查询jar")
    @RequestMapping(value="/queryJar",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> queryJar(@RequestParam("mainResourceId") Long mainResourceId,
                                   @RequestParam("motorVersionId") Long motorVersionId){
        return jarFileServiceImpl.queryJar(mainResourceId,motorVersionId);
    }

    @ApiOperation(value="下载jar")
    @RequestMapping(value="/download",method= RequestMethod.POST)
    @AccessLogAnnotation
    public ResponseEntity downloadJar(@RequestBody JarFileDto jarFileDto){
        return jarFileServiceImpl.downloadJar(jarFileDto);
    }

}
