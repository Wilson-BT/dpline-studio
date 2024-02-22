package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.handsome.dao.entity.Project;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * project mapper interface
 */
public interface ProjectMapper extends BaseMapper<Project> {
    /**
     * query project detail by code
     * @param projectCode projectCode
     * @return project
     */
    Project queryByCode(@Param("projectCode") long projectCode);

    /**
     * TODO: delete
     * query project detail by id
     * @param projectId projectId
     * @return project
     */
    Project queryDetailById(@Param("projectId") int projectId);

    /**
     * query project detail by code
     * @param projectCode projectCode
     * @return project
     */
    Project queryDetailByCode(@Param("projectCode") long projectCode);

    /**
     * query project by name
     * @param projectName projectName
     * @return project
     */
    Project queryByName(@Param("projectName") String projectName);

    // TODO 根据项目名或者描述模糊查询项目列表
    /**
     *
     *
     * project page
     * @param page page
     * @param userId userId
     * @param searchName searchName
     * @return project Ipage
     */
    IPage<Project> queryProjectListPaging(IPage<Project> page,
                                          @Param("userId") int userId,
                                          @Param("searchName") String searchName);

    /**
     *  query create project user
     * @param userId userId
     * @return project list
     */
    List<Project> queryProjectCreatedByUser(@Param("userId") int userId);

    /**
     * query authed project list by userId
     * @param userId userId
     * @return project list
     */
    List<Project> queryAuthedProjectListByUserId(@Param("userId") int userId);

    /**
     * query relation project list by userId
     * @param userId userId
     * @return project list
     */
    List<Project> queryRelationProjectListByUserId(@Param("userId") int userId);

    /**
     * query project except userId
     * @param userId userId
     * @return project list
     */
    List<Project> queryProjectExceptUserId(@Param("userId") int userId);

    /**
     * query project list by userId
     * @param userId
     * @return
     */
    List<Project> queryProjectCreatedAndAuthorizedByUserId(@Param("userId") int userId);


    /**
     * query all project
     * @return projectList
     */
    List<Project> queryAllProject();
}
