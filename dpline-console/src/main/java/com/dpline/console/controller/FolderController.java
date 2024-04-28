package com.dpline.console.controller;

import com.dpline.common.util.Result;
import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.FolderServiceImpl;
import com.dpline.dao.dto.FolderDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags="目录管理")
@RequestMapping(value = "/folder")
public class FolderController {

    @Autowired
    private FolderServiceImpl folderService;

    @ApiOperation(value="添加目录")
    @RequestMapping(value="/addFolder",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> addFolder(@RequestBody @Valid FolderDto folderDto){
        return folderService.addFolder(folderDto);
    }

    @ApiOperation(value="查询目录(树)")
    @RequestMapping(value="/queryFolder",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> queryFolder(@RequestBody FolderDto folderDto, @RequestHeader(value="projectId") Long projectId){
        folderDto.setProjectId(projectId);
        return folderService.queryFolder(folderDto);
    }



    @ApiOperation(value="修改目录")
    @RequestMapping(value="/updateFolder",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateFolder(@RequestBody FolderDto folderDto){
        return folderService.updateFolder(folderDto);
    }

    @ApiOperation(value="删除目录")
    @RequestMapping(value="/deleteFolder",method= RequestMethod.POST)
    public Result<Object> deleteFolder(@RequestBody FolderDto folderDto){
        return folderService.deleteFolder(folderDto);
    }

    @ApiOperation(value="文件/目录搜索")
    @RequestMapping(value="/searchFolder",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> searchFolder(@RequestBody FolderDto folderBO){
        return folderService.searchFolder(folderBO);
    }

    @ApiOperation(value="目录搜索")
    @RequestMapping(value="/searchFolders",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> searchFolders(@RequestBody FolderDto folderBO){
        return folderService.searchFolders(folderBO);
    }
}
