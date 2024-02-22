package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@TableName("dpline_relation_k8s_namespace_user")
public class K8sNameSpacesUser {

    @TableId(value = "id")
    private long id;

    /**
     * k8s NameSpace id
     */
    @TableField("k8s_namespace_id")
    private long k8sNameSpaceId;

    /**
     * user id
     */
    private int userId;

    /**
     * create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getK8sNameSpaceId() {
        return k8sNameSpaceId;
    }

    public void setK8sNameSpaceId(long k8sNameSpaceId) {
        this.k8sNameSpaceId = k8sNameSpaceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public K8sNameSpacesUser() {
    }

    public K8sNameSpacesUser(long id, long k8sNameSpaceId, int userId, Date createTime, Date updateTime) {
        this.id = id;
        this.k8sNameSpaceId = k8sNameSpaceId;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "K8sNameSpacesUser{" +
                "id=" + id +
                ", k8sNameSpaceId=" + k8sNameSpaceId +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
