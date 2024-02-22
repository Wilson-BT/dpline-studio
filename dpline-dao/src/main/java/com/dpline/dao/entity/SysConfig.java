package com.dpline.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dpline.dao.generic.GenericModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <pre>
 * 系统配置表实体类
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dpline_sys_config")
public class SysConfig extends GenericModel<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字段名称：配置类型key
     *
     * 数据库字段信息:config_key VARCHAR(255)
     */
    private String configKey;

    /**
     * 字段名称：配置value
     *
     * 数据库字段信息:config_value TEXT(65535)
     */
    private String configValue;
}