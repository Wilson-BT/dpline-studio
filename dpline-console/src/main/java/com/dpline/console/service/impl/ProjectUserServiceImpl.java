package com.dpline.console.service.impl;

import com.dpline.common.util.Asserts;
import com.dpline.console.service.GenericService;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.entity.User;
import com.dpline.dao.mapper.ProjectUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProjectUserServiceImpl extends GenericService<ProjectUser,Long> {


    public ProjectUserServiceImpl(@Autowired ProjectUserMapper projectUserMapper) {
        super(projectUserMapper);
    }

    public ProjectUserMapper getMapper() {
        return (ProjectUserMapper)genericMapper;
    }

    public void deleteRelationByUserCode(String userCode) {
        this.getMapper().deleteByUserCode(userCode);
    }


    public boolean hasQueryPerm(User operateUser, Long projectId) {
        ProjectUser projectUser = this.getMapper().selectProjectUser(projectId, operateUser.getUserCode());
        return  Asserts.isNotNull(projectUser) || isAdmin(operateUser);
    }
}
