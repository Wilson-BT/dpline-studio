

package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.handsome.common.enums.AlertStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TableName("dpline_alert")
public class Alert {
    /**
     * primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /**
     * title
     */
    @TableField(value = "title")
    private String title;

    /**
     * content
     */
    @TableField(value = "content")
    private String content;

    /**
     * alert_status
     */
    @TableField(value = "alert_status")
    private AlertStatus alertStatus;
    /**
     * log
     */
    @TableField(value = "log")
    private String log;
    /**
     * alertgroup_id
     */
    @TableField("alertgroup_id")
    private int alertGroupId;

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
    @TableField(exist = false)
    private Map<String, Object> info = new HashMap<>();

    public Alert() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AlertStatus getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(AlertStatus alertStatus) {
        this.alertStatus = alertStatus;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public int getAlertGroupId() {
        return alertGroupId;
    }

    public void setAlertGroupId(int alertGroupId) {
        this.alertGroupId = alertGroupId;
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

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Alert alert = (Alert) o;

        if (id != alert.id) {
            return false;
        }
        if (alertGroupId != alert.alertGroupId) {
            return false;
        }
        if (!title.equals(alert.title)) {
            return false;
        }
        if (!content.equals(alert.content)) {
            return false;
        }
        if (alertStatus != alert.alertStatus) {
            return false;
        }
        if (!log.equals(alert.log)) {
            return false;
        }
        if (!createTime.equals(alert.createTime)) {
            return false;
        }
        return updateTime.equals(alert.updateTime) && info.equals(alert.info);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + alertStatus.hashCode();
        result = 31 * result + log.hashCode();
        result = 31 * result + alertGroupId;
        result = 31 * result + createTime.hashCode();
        result = 31 * result + updateTime.hashCode();
        result = 31 * result + info.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Alert{"
                + "id="
                + id
                + ", title='"
                + title + '\''
                + ", content='"
                + content
                + '\''
                + ", alertStatus="
                + alertStatus
                + ", log='"
                + log
                + '\''
                + ", alertGroupId="
                + alertGroupId
                + '\''
                + ", createTime="
                + createTime
                + ", updateTime="
                + updateTime
                + ", info="
                + info
                + '}';
    }
}
