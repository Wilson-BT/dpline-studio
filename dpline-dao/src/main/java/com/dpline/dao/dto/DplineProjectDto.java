package com.dpline.dao.dto;

import com.dpline.dao.generic.GenericModel;
import com.dpline.dao.entity.ProjectUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <pre>
 * 项目实体类
 * 数据库表名称：sdp_project
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DplineProjectDto extends GenericModel<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字段名称：项目名称
     *
     * 数据库字段信息:project_name VARCHAR(255)
     */
    private String projectName;


//    private String projectCode;

    /**
     * 字段名称：创建人
     */
    private String projectOwner;
    /**
     * 字段名称：用户
     */
    private String projectUsers;

    /**
     * 字段名称：项目用户角色
     */
    private ProjectUser currentUserRole;

    private Integer userCount;
    /**
     * 在线作业数
     */
    private Integer onlineJobNum;


    private String description;

    public DplineProjectDto() {
    }	
}