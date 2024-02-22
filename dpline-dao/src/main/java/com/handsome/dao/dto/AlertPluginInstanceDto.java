

package com.handsome.dao.dto;

import java.util.Date;

/**
 * AlertPluginInstanceVO
 */
public class AlertPluginInstanceDto {

    /**
     * id
     */
    private int id;

    /**
     * plugin_define_id
     */
    private int pluginDefineId;

    /**
     * alert plugin instance name
     */
    private String instanceName;

    /**
     * plugin_instance_params
     */
    private String pluginInstanceParams;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update_time
     */
    private Date updateTime;

    /**
     * alert plugin name
     */
    private String alertPluginName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPluginDefineId() {
        return pluginDefineId;
    }

    public void setPluginDefineId(int pluginDefineId) {
        this.pluginDefineId = pluginDefineId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getPluginInstanceParams() {
        return pluginInstanceParams;
    }

    public void setPluginInstanceParams(String pluginInstanceParams) {
        this.pluginInstanceParams = pluginInstanceParams;
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

    public String getAlertPluginName() {
        return alertPluginName;
    }

    public void setAlertPluginName(String alertPluginName) {
        this.alertPluginName = alertPluginName;
    }
}
