package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.ReleaseState;

import java.util.Date;

@TableName("dpline_flink_version")
public class FlinkVersion {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * flink name
     */
    private String flinkName;
    /**
     * flink home path
     */
    private String flinkPath;
    /**
     * whether online
     */
    private ReleaseState online;
    /**
     * real version
     */
    private String realVersion;

    /**
     * create time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * update time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String description;

    public String getRealVersion() {
        return realVersion;
    }

    public void setRealVersion(String realVersion) {
        this.realVersion = realVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public FlinkVersion() {
    }

    public FlinkVersion(int id, String flinkName, String flinkPath, ReleaseState online, Date createTime, Date updateTime) {
        this.id = id;
        this.flinkName = flinkName;
        this.flinkPath = flinkPath;
        this.online = online;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlinkName() {
        return flinkName;
    }

    public void setFlinkName(String flinkName) {
        this.flinkName = flinkName;
    }

    public String getFlinkPath() {
        return flinkPath;
    }

    public void setFlinkPath(String flinkPath) {
        this.flinkPath = flinkPath;
    }

    public ReleaseState getReleaseState() {
        return online;
    }

    public void setReleaseState(ReleaseState online) {
        this.online = online;
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

    public ReleaseState getOnline() {
        return online;
    }

    public void setOnline(ReleaseState online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "FlinkVersion{" +
                "id=" + id +
                ", flinkName='" + flinkName + '\'' +
                ", flinkPath='" + flinkPath + '\'' +
                ", online=" + online +
                ", realVersion=" + realVersion +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

