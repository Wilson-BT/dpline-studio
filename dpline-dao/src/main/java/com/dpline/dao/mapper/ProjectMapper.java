package com.dpline.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.dpline.dao.bo.ProjectCount;
import com.dpline.dao.dto.ProjectInfo;
import com.dpline.dao.dto.ProjectUserInfo;
import com.dpline.dao.entity.Project;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.generic.GenericMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("mysql")
@Repository
public interface ProjectMapper extends GenericMapper<Project, Long> {


    /**
     * 获取任务、项目统计数
     * @return
     * @param projectName
     * @param userCode
     */
    ProjectCount getTaskProjectStat(@Param("projectName") String projectName,
                                    @Param("userCode") String userCode);


    /**
     * 获取指定的 项目权限
     *
     * @return
     */
    List<ProjectInfo> getAuthedProjectList(@Param("projectIdList") List<Long> projectIdList, @Param("projectName") String projectName);

    List<Project> getProjects(@Param("userCode") String userCode,
                              @Param("projectName") String projectName);

    List<ProjectUser> selectByUserCode(@Param("userCode") String userCode);

    List<ProjectUserInfo> selectByProjectId(@Param("projectIdList") List<Long> projectIdList);
}
