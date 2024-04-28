package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("dpline_project_user")
public class ProjectUser extends GenericModel<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * user id
     */
    private String userCode;

    private Long projectId;
    /**
     * 用户角色，
     */
    private Integer userRole;


    public ProjectUser() {
    }

}