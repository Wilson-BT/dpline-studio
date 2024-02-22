package com.handsome.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.handsome.dao.entity.ProjectUser;
import org.apache.ibatis.annotations.Param;

/**
 * project user mapper interface
 */
public interface ProjectUserMapper extends BaseMapper<ProjectUser> {

    /**
     * delte prject user relation
     *
     * @param projectId projectId
     * @param userId userId
     * @return delete result
     */
    int deleteProjectRelation(@Param("projectId") int projectId,
                              @Param("userId") int userId);

    /**
     * query project relation
     *
     * @param projectId projectId
     * @param userId userId
     * @return project user relation
     */
    ProjectUser queryProjectRelation(@Param("projectId") int projectId,
                                     @Param("userId") int userId);
}
