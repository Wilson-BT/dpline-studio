package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * FlinkVersion 和 用户关系表
 */
@TableName("dpline_relation_flink_version_user")
public class FlinkVersionUser {
    /**
     * id
     */
    @TableId(value="id", type= IdType.AUTO)
    private int id;
    /**
     * user id
     */
    private int userId;
    /**
     * flink version id
     */
    private int flinkVersionId;
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

    public int getFlinkVersionId() {
        return flinkVersionId;
    }

    public void setFlinkVersionId(int flinkVersionId) {
        this.flinkVersionId = flinkVersionId;
    }

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

    @Override
    public String toString() {
        return "FlinkVersionUser{" +
                "id=" + id +
                ", userId=" + userId +
                ", flinkVersionId=" + flinkVersionId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
