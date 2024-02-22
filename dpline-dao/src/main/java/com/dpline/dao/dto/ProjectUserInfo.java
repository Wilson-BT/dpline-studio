package com.dpline.dao.dto;

import com.dpline.dao.entity.ProjectUser;
import lombok.Data;

@Data
public class ProjectUserInfo extends ProjectUser {

    private String userName;

    private Integer isAdmin;

    private Integer isLeader;

    private String phone;

}
