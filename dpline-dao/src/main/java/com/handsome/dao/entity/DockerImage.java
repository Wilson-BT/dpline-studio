package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dpline_docker_image")
public class DockerImage {

    @TableId(value = "id",type = IdType.AUTO)
    private int id;

    @TableField("alias_name")
    private String aliasName;

    @TableField(value = "image_name")
    private String imageName;

    @TableField(value = "register_address")
    private String registerAddress;

    @TableField("register_user")
    private String registerUser;

    /**
     * 注册的密码信息需要屏蔽掉
     */
    @JsonIgnore
    @TableField("register_password")
    private String registerPassword;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
