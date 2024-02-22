package com.dpline.dao.dto;


import com.dpline.dao.entity.Project;
import com.dpline.dao.entity.ProjectUser;
import com.dpline.dao.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectInfo extends Project implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用于进项项目列表排序使用
     * 最大角色
     */
    private int currentUserRole;
    /**
     * 在线作业数
     */
    private Integer onlineJobNum;

    private Integer userCount;

    private String ownerName;

    private List<ProjectUserInfo> userList;

    public ProjectInfo() {
    }	
}