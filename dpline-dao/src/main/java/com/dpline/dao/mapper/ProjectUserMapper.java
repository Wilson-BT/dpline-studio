package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface ProjectUserMapper extends GenericMapper<ProjectUser,Long> {
    ProjectUser selectIsExist(ProjectUser newProjectUser);

    List<ProjectUser> getProjectUserRelByProjectId(@Param("projectId") Long projectId,
                                                   @Param("userRole") Integer userRole);

    Integer deleteByProjectId(@Param("projectId") Long projectId,@Param("userRole") Integer userRole);

    Integer deleteProjectUser(@Param("projectId") Long projectId,@Param("userCode") String userCode);

    List<ProjectUser> selectByProjectId(@Param("projectId") Long projectId);

    ProjectUser selectProjectUser(@Param("projectId") Long projectId,@Param("userCode") String userCode);


    Integer deleteByUserCode(@Param("userCode") String userCode);
}
