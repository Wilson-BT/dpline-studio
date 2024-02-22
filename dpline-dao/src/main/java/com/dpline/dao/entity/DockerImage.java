package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("dpline_docker_image")
public class DockerImage extends GenericModel<Long> implements Serializable {

    @TableField("short_name")
    private String shortName;

    @TableField(value = "image_name")
    private String imageName;

    @TableField(value = "register_address")
    private String registerAddress;

    @TableField("register_user")
    private String registerUser;

    /**
     * 注册的密码信息需要屏蔽掉
     */
//    @JsonIgnore
    @TableField("register_password")
    private String registerPassword;

    @TableField("motor_version_id")
    private long motorVersionId;

    @TableField("motor_type")
    private String motorType;

    @TableField(exist = false)
    private String motorRealVersion;

}
