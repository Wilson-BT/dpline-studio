package com.dpline.console.controller;

import com.dpline.console.aspect.AccessLogAnnotation;
import com.dpline.console.service.impl.ProjectServiceImpl;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.dto.ProjectUserInfo;
import com.dpline.dao.entity.Project;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.common.util.Result;
import com.dpline.dao.rto.DplineProjectRto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "项目管理")
@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private ProjectServiceImpl projectService;

    @ApiOperation(value = "项目管理-历史使用projectId查询，前端使用跳转文件编辑页面")
    @RequestMapping(value = "/history",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> getProjectHistory() {
        Result<Object> data = new Result<Object>();
        data.ok();
        data.setData(this.projectService.getProjectHistory(ContextUtils.get().getUser()));
        return data;
    }

    @ApiOperation(value = "项目管理-项目列表")
    @RequestMapping(value = "/list",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> projectList(@RequestBody DplineProjectRto dplineProjectRto) {
        return this.projectService.projectList(ContextUtils.get().getUser(),dplineProjectRto);
    }

    @ApiOperation(value = "项目管理-添加项目")
    @RequestMapping(value = "/add",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> addProject(@RequestBody DplineProjectRto dplineProjectRto) {
        return this.projectService.add(dplineProjectRto);
    }


    @ApiOperation(value = "项目管理-删除项目")
    @RequestMapping(value = "/delete",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteProject(@RequestParam("projectId") String projectId) {
        Result<Object> result = new Result<>();
        result.ok();
        result.setData(this.projectService.delete(Long.parseLong(projectId)));
        return result;
    }

    @ApiOperation(value = "项目管理-修改项目")
    @RequestMapping(value = "/update",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> updateProject(@RequestBody DplineProjectRto dplineProjectRto) {
        return this.projectService.updateProject(dplineProjectRto);
    }

    @ApiOperation(value = "项目管理-项目成员角色转变")
    @RequestMapping(value = "/changeRoles",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> changeRoles(@RequestBody List<ProjectUserInfo> projectUserInfo) {
        return this.projectService.changeRoles(projectUserInfo);
    }

    @ApiOperation(value = "项目管理-项目获取用户")
    @RequestMapping(value = "/projectUserInfo",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> projectUser(@RequestBody ProjectUser projectUser) {
        return this.projectService.getProjectUserInfo(projectUser);
    }

    @ApiOperation(value = "项目管理-删除项目user")
    @RequestMapping(value = "/deleteProjectUser",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> deleteProjectUser(@RequestParam("projectId") Long projectId,
                                            @RequestParam("userCode") String userCode
                                            ) {
        return this.projectService.deleteProjectUser(projectId,userCode);

    }

    @ApiOperation(value = "项目管理-项目名称查询")
    @RequestMapping(value = "/getProjects",method= RequestMethod.POST)
    @AccessLogAnnotation
    public Result<Object> getProjects(@RequestBody(required = false) Project project) {
        return this.projectService.getProjects(project);
    }

}
