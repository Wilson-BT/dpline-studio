package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.handsome.common.enums.AlertType;

import java.util.Date;

/**
 * dpline_alert_instance
 */
@TableName("dpline_alert_instance")
public class AlertInstance {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * alert plugin instance name
     */
    @TableField("instance_name")
    private String instanceName;

    /**
     * plugin_instance_params
     */
    @TableField("instance_params")
    private String instanceParams;

    /**
     * create_time
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * update_time
     */
    @TableField("update_time")
    private Date updateTime;

    @TableField("alter_type")
    private AlertType alertType;

    public AlertInstance(int id, AlertType alertType, String instanceParams, String instanceName, Date updateDate) {
        this.id = id;
        this.instanceParams = instanceParams;
        this.updateTime = updateDate;
        this.instanceName = instanceName;
        this.alertType = alertType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstanceParams() {
        return instanceParams;
    }

    public void setInstanceParams(String instanceParams) {
        this.instanceParams = instanceParams;
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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public AlertType getAlterType() {
        return alertType;
    }

    public void setAlterType(AlertType alertType) {
        this.alertType = alertType;
    }
}

