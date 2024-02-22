package com.dpline.console.service.impl;

import com.dpline.common.enums.ProjectUserEnum;
import com.dpline.common.enums.Status;
import com.dpline.common.enums.UserType;
import com.dpline.common.util.*;
import com.dpline.console.exception.ServiceException;
import com.dpline.console.service.GenericService;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.bo.ProjectCount;
import com.dpline.dao.dto.DplineProjectDto;
import com.dpline.dao.dto.ProjectInfo;
import com.dpline.dao.dto.ProjectUserInfo;
import com.dpline.dao.entity.Project;
import com.dpline.dao.entity.User;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.mapper.ProjectMapper;
import com.dpline.dao.rto.DplineProjectRto;
import net.minidev.json.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProjectServiceImpl extends GenericService<Project, Long> {

    @Autowired
    UsersServiceImpl userService;

    @Autowired
    ProjectUserServiceImpl projectUserService;

    public ProjectServiceImpl(@Autowired ProjectMapper projectMapper) {
        super(projectMapper);
    }

    /**
     * 获取历史项目
     *
     * @return
     */
    public ProjectUser getProjectHistory(User logUser) {
        ProjectUser projectUser = userService.getMapper().getProjectHistory(logUser.getUserCode());
        if (Objects.isNull(projectUser)) {
            //如果没有查到最近访问的项目，则查询该用户有权限的项目
            projectUser = userService.getMapper().queryProjectsByUser(logUser.getUserCode());
        }
        return projectUser;
    }

    /**
     *
     *  1. 获取用户有权限的项目, 转为key=该项目下,value=用户和项目的关系,的一个map（如果用户是admin，直接返回所有项目列表）
     * 2. 根据查询到的所有项目Id，查询项目详细信息
     * 3. 根据项目Id列表，查询所有用户
     * 4. 转为 Map<项目ID,有权限的用户列表> ，即 一个项目下有多少用户
     *
     * @param loginUser
     * @param dplineProjectRto
     * @return
     */
    public Result<Object> projectList(User loginUser, DplineProjectRto dplineProjectRto) {
        Result<Object> result = new Result<>();
        dplineProjectRto.setUserId(loginUser.getId());
        dplineProjectRto.setUserCode(loginUser.getUserCode());

        // 该用户有哪些项目的权限
        List<ProjectUser> projectUserList = this.getMapper().selectByUserCode(loginUser.getUserCode());
        Map<Long, ProjectUser> projectUserRelationMap = projectUserList.stream().collect(Collectors.toMap(ProjectUser::getProjectId, t -> t));

        // 找到所有应该展示的项目
        Pagination<ProjectInfo> instanceProject;
        // 如果用户是 admin, 查询所有项目
        if (isAdmin(loginUser)){
            instanceProject = pageHelper(this.getMapper().getAuthedProjectList(null, dplineProjectRto.getProjectName()),
                dplineProjectRto.getPage(),
                dplineProjectRto.getPageSize());
        } else {
            // 非admin权限，则查询有权限的项目
            List<Long> projectIdList = projectUserList.stream().map(ProjectUser::getProjectId).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(projectIdList)){
                instanceProject =  new Pagination<>();
            }else {
                instanceProject = pageHelper(this.getMapper().getAuthedProjectList(projectIdList, dplineProjectRto.getProjectName()),
                    dplineProjectRto.getPage(),
                    dplineProjectRto.getPageSize());
            }
        }
        // 根据项目列表，获取到所有用户
        List<Long> projectIdList = instanceProject.getRows().stream().map(ProjectInfo::getId).collect(Collectors.toList());

        // 将要展示的项目下所有的用户，转为map形式
        List<ProjectUserInfo> projectUserInfoList = this.getMapper().selectByProjectId(projectIdList);
        // Map<项目ID,有权限的用户列表>
        HashMap<Long, List<ProjectUserInfo>> projectIdUserMap = new HashMap<>();
        projectUserInfoList.forEach(
            projectUserInfo -> {
                List<ProjectUserInfo> projectUserInfos;
                if(projectIdUserMap.containsKey(projectUserInfo.getProjectId())){
                    projectUserInfos = projectIdUserMap.get(projectUserInfo.getProjectId());
                } else {
                    projectUserInfos = new ArrayList<>();
                }
                projectUserInfos.add(projectUserInfo);
                projectIdUserMap.putIfAbsent(projectUserInfo.getProjectId(),projectUserInfos);
            }
        );

        ArrayList<DplineProjectDto> resArr = new ArrayList<>();
        // 组织返回数据，获取到所有任务的环节
        instanceProject.getRows().forEach(
            projectInfo -> {
                DplineProjectDto dplineProjectDto = new DplineProjectDto();
                BeanUtils.copyProperties(projectInfo,dplineProjectDto);
                // 当前用户是admin，但当前用户不是项目的负责人，关系表中不存在该项目, 则赋予管理者角色
                if(!projectUserRelationMap.containsKey(projectInfo.getId())){
                    ProjectUserInfo projectUserInfo = new ProjectUserInfo();
                    projectUserInfo.setProjectId(projectInfo.getId());
                    projectUserInfo.setUserRole(ProjectUserEnum.ORGANIZE_USER.getCode());
                    projectUserInfo.setIsLeader(ProjectUserEnum.ORGANIZE_USER.getCode());
                    projectUserInfo.setUserCode(loginUser.getUserCode());
                    projectUserInfo.setIsAdmin(loginUser.getIsAdmin());
                    dplineProjectDto.setCurrentUserRole(projectUserInfo);
                }
                // 准备遍历所有用户，划分管理者和普通用户
                List<ProjectUserInfo> projectUserInfos = projectIdUserMap.get(projectInfo.getId());
                // 所有用户
                ArrayList<ProjectUser> managers = new ArrayList<>();
                ArrayList<ProjectUser> generals = new ArrayList<>();
                projectUserInfos.forEach(projectUserInfo -> {
                    if (StringUtils.isBlank(projectUserInfo.getUserName())) {
                        return;
                    }
                    // 如果是管理人员
                    if (ProjectUserEnum.ORGANIZE_USER.getCode().equals(projectUserInfo.getUserRole())) {
                        managers.add(projectUserInfo);
                    }
                    // 如果是普通用户
                    if (ProjectUserEnum.GENERAL_USER.getCode().equals(projectUserInfo.getUserRole())) {
                        generals.add(projectUserInfo);
                    }
                    // 如果当前用户拥有权限
                    if (projectUserInfo.getUserCode().equals(loginUser.getUserCode())) {
                        dplineProjectDto.setCurrentUserRole(projectUserInfo);
                    }
                });
                // 管理
                dplineProjectDto.setProjectOwner(JSONUtils.toJsonString(managers));
                // 用户,管理员也是用户的一部分
                generals.addAll(managers);
                dplineProjectDto.setProjectUsers(JSONUtils.toJsonString(generals));
                resArr.add(dplineProjectDto);
            }
        );
        // 首先根据用户和项目之间的关系查到到所有项目
        //        if (CollectionUtils.isEmpty(resArr)) {
        //            return null;
        //        }
        //统计实际的项目数量，作业数量，用户数量
        ProjectCount projectCount = this.getMapper().getTaskProjectStat(
            dplineProjectRto.getProjectName(),
            dplineProjectRto.getUserCode());

        projectCount.setRows(resArr);
        projectCount.setRowTotal(instanceProject.getRowTotal());
        projectCount.setPageTotal(
            projectCount.getRowTotal() % dplineProjectRto.getPageSize() == 0
            ? projectCount.getRowTotal() / dplineProjectRto.getPageSize()
            : (projectCount.getRowTotal() / dplineProjectRto.getPageSize() + 1)
        );
        return result.setData(projectCount).ok();
    }

    @Transactional
    public Result<Object> add(DplineProjectRto dplineProjectRto) {
        Result<Object> result = new Result<>();
        // 如果没有设置leader的话
        Project project = new Project();
        BeanUtils.copyProperties(dplineProjectRto,project);
        long projectId;
        try {
            projectId = CodeGenerateUtils.getInstance().genCode();
            project.setId(projectId);
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            e.printStackTrace();
        }
        insert(project);
        // 项目管理员角色,创建者默认是负责人
        HashMap<String, byte[]> leaderMap = new HashMap<>();
        List<ProjectUser> projectLeader = dplineProjectRto.getProjectLeader();
        if(CollectionUtils.isEmpty(projectLeader)){
            ProjectUser projectUser = new ProjectUser();
            projectUser.setProjectId(project.getId());
            projectUser.setUserRole(ProjectUserEnum.ORGANIZE_USER.getCode());
            projectUser.setUserCode(ContextUtils.get().getUserCode());
            projectLeader.add(projectUser);
        }
        List<ProjectUser> projectLeaderRel = projectLeader.stream().peek(
            projectUser -> {
                // 人物去重
                projectUser.setProjectId(project.getId());
                projectUser.setUserRole(ProjectUserEnum.ORGANIZE_USER.getCode());
                leaderMap.put(projectUser.getUserCode(),new byte[0]);
            }
        ).collect(Collectors.toList());
        // 普通用户 和 leader 去重， 在普通用户中去除 已经是leader的用户
        List<ProjectUser> projectUserRel = dplineProjectRto.getProjectUsers().stream().map(x -> {
            if (!leaderMap.containsKey(x.getUserCode())) {
                x.setProjectId(project.getId());
                x.setUserRole(ProjectUserEnum.GENERAL_USER.getCode());
                return x;
            }
            return null;
        }).filter(Asserts::isNotNull).collect(Collectors.toList());
        projectUserRel.addAll(projectLeaderRel);
        projectUserService.insertBatch(projectUserRel);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    public ProjectMapper getMapper() {
        return (ProjectMapper) super.genericMapper;
    }

    @Transactional
    public Result<Object> changeRoles(List<ProjectUserInfo> projectUserInfo) {
        Result<Object> result = new Result<>();
        User logUser = ContextUtils.get().getUser();
        Set<Long> projectIdSet = projectUserInfo.stream().map(ProjectUser::getProjectId).collect(Collectors.toSet());

        if(!checkPermission(projectIdSet, logUser)){
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        projectUserInfo.forEach(x -> {
            // 检查 所有参数是否规范
            checkProjectUser(x);
            // 检查当前用户对该项目是否有权限
            // 根据传进来数据是否含有isAdmin判断新增还是修改角色
            ProjectUser projectUser = new ProjectUser();
            projectUser.setUserCode(x.getUserCode());
            projectUser.setProjectId(x.getProjectId());
            ProjectUser projectUsers = projectUserService.getMapper().selectIsExist(projectUser);
            //有的就直接update
            if (Asserts.isNull(projectUsers)) {
                projectUser.setUserRole(Asserts.isNotNull(x.getIsLeader()) ? x.getIsLeader()
                    :ProjectUserEnum.GENERAL_USER.getCode());
                projectUserService.insert(projectUser);
                return;
            }
            projectUser.setId(projectUsers.getId());
            projectUser.setUserRole(x.getIsLeader());
            projectUserService.update(projectUser);
        });
        result.ok();
        return result;
    }

    private void checkProjectUser(ProjectUserInfo projectUser) {

        if (Asserts.isNull(projectUser.getProjectId())) {
            throw new ServiceException(Status.REQUEST_PARAMS_NOT_VALID_ERROR, "projectId");
        }
        if (StringUtils.isEmpty(projectUser.getUserCode())) {
            throw new ServiceException(Status.REQUEST_PARAMS_NOT_VALID_ERROR, "userCode");
        }
    }

    private boolean checkPermission(Collection<Long> projectIdCollect, User user) {
        if (user.getIsAdmin() == (UserType.ADMIN_USER.getCode())) {
            return true;
        }
        List<ProjectUserInfo> projectUserList = userService.getProjectUser(user.getUserCode(), projectIdCollect);
        if (CollectionUtils.isEmpty(projectUserList)) {
            return false;
        }
        AtomicBoolean permissionFlag = new AtomicBoolean(true);
        projectUserList.forEach(
            x -> {
                if (x.getIsLeader().equals(ProjectUserEnum.GENERAL_USER.getCode())) {
                    permissionFlag.set(false);
                }
        });
        return permissionFlag.get();
    }

    public Result<Object> updateProject(DplineProjectRto dplineProjectRto) {
        Result<Object> result = new Result<>();
        putMsg(result,Status.SUCCESS);
        Long projectId = dplineProjectRto.getProjectId();
        Project project = new Project();
        BeanUtils.copyProperties(dplineProjectRto,project);
        project.setId(projectId);
        updateSelective(project);
        // 首先，在 dplineProjectRto.getProjectLeader() 中 对 dplineProjectRto.getProjectUsers() 去重
        HashMap<String, ProjectUser> userProjectMap = new HashMap<>();
        // 用户列表
        dplineProjectRto.getProjectUsers()
            .forEach(x -> {
                userProjectMap.put(x.getUserCode(),x);
            });
        // leader 中存在，则 userProjectMap delete
        // leader 列表 ，用户列表中删除leader
        dplineProjectRto.getProjectLeader().forEach(
            x->{
                userProjectMap.remove(x.getUserCode());
            }
        );
        List<ProjectUser> projectUsers = new ArrayList<>(userProjectMap.values());
        // 先处理负责人，
        updateProjectRole(dplineProjectRto.getProjectLeader(),projectId,ProjectUserEnum.ORGANIZE_USER.getCode());
        // 再处理用户
        updateProjectRole(projectUsers,projectId,ProjectUserEnum.GENERAL_USER.getCode());
        return result;
    }

    private void updateProjectRole(List<ProjectUser> projectLeader, Long projectId,Integer userProjectRole) {
        // 先将之前的置为失效
        projectUserService.getMapper().deleteByProjectId(projectId,userProjectRole);
        // 然后将现在的插入
        projectLeader.forEach(
            x -> {
                x.setProjectId(projectId);
                x.setUserRole(userProjectRole);
            }
        );
        projectUserService.insertBatch(projectLeader);
    }

    /**
     * 获取项目和用户的关联信息
     *
     * @param project
     * @return
     */
    public Result<Object> getProjectUserInfo(ProjectUser project) {
        Result<Object> result = new Result<>();
        // 根据
        Long projectId = project.getProjectId();
        // 获取到
        List<ProjectUser> projectUsers = projectUserService.getMapper().selectByProjectId(projectId);
        result.ok();
        result.setData(projectUsers);
        return result;
    }

    /**
     * 删除
     *
     * @return
     */
    public Result<Object> deleteProjectUser(Long projectId, String userCode) {
        // 先判断权限
        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        ArrayList<Long> projectIdList = new ArrayList<>();
        if(!checkPermission(projectIdList, user)){
            putMsg(result,Status.USER_NO_OPERATION_PROJECT_PERM);
            return result;
        }
        projectUserService.getMapper().deleteProjectUser(projectId,userCode);
        result.ok();
        return result;
    }

    public Result<Object> getProjects(Project project) {
        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        String userCode = user.getUserCode();
        if (Objects.nonNull(userCode)) {
            if (UserType.ADMIN_USER.getCode() == user.getIsAdmin()) {
                userCode = null;
            }
        }
        String projectName = Optional.ofNullable(project).map(Project::getProjectName).orElse(null);
        List<Project> projects = this.getMapper().getProjects(userCode, projectName);
        result.ok();
        result.setData(projects);
        return result;
    }

    /**
     * delete TODO 查看用户有无权限，有权限的话，需要查看项目下面有没有正在运行的任务
     * @param project
     * @return
     */
    public Result<Object> delete(Project project) {
        Result<Object> result = new Result<>();
        User user = ContextUtils.get().getUser();
        String userCode = user.getUserCode();
//        if (Objects.nonNull(userCode)) {
//            if (UserType.ADMIN_USER.getCode() == user.getIsAdmin()) {
//                userCode = null;
//            }
//        }
//        String projectName = Optional.ofNullable(project).map(Project::getProjectName).orElse(null);
//        List<Project> projects = this.getMapper().getProjects(userCode, projectName);
        //




        return result.ok();
    }



}
