package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("dpline_relation_project_user")
public class ProjectUser {
    /**
     * id
     */
    @TableId(value="id", type=IdType.AUTO)
    private int id;

    @TableField("user_id")
    private int userId;

    @TableField("project_id")
    private int projectId;

    /**
     * project name
     */
    @TableField(exist = false)
    private String projectName;

    /**
     * user name
     */
    @TableField(exist = false)
    private String userName;

    /**
     * permission
     */
    private int perm;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }

    @Override
    public String toString() {
        return "ProjectUser{"
               + "id=" + id
               + ", userId=" + userId
               + ", projectId=" + projectId
               + ", projectName='" + projectName + '\''
               + ", userName='" + userName + '\''
               + ", perm=" + perm
               + ", createTime=" + createTime
               + ", updateTime=" + updateTime
               + '}';
    }
}
