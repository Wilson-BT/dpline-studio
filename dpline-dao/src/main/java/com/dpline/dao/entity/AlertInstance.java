package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;

/**
 * dpline_alert_instance
 */
@Data
@TableName("dpline_alert_instance")
public class AlertInstance extends GenericModel<Long> {

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


    @TableField("alert_type")
    private String alertType;

    /**
     * 运行模式
     */
    @TableField(exist = false)
    private String alertMode;

}

