package com.dpline.dao.entity;

import com.dpline.dao.generic.GenericModel;
import lombok.Data;

@Data
public class JobMetricAlertRule extends GenericModel<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称：作业ID
     *
     * 数据库字段信息:job_id BIGINT(19)
     */
    private Long jobId;

    /**
     * 字段名称：规则类型
     */
    private String ruleGenerateType;

    /**
     * 字段名称：规则名称
     *
     * 数据库字段信息:rule_name VARCHAR(128)
     */
    private String ruleName;

    /**
     * 字段名称：规则描述
     *
     * 数据库字段信息:rule_desc VARCHAR(255)
     */
    private String ruleDesc;

    private String indexName;

    /**
     * 字段名称：规则内容
     *
     * 数据库字段信息:rule_content VARCHAR(255)
     */
    private String ruleContent;

    /**
     * 字段名称：生效时间，例如00:00-10:00
     *
     * 数据库字段信息:effective_time VARCHAR(40)
     */
    private String effectiveTime;

    /**
     * 字段名称：告警频率(分钟)
     *
     * 数据库字段信息:alert_rate INT(10)
     */
    private Integer alertRate;

    /**
     * 字段名称：通知方式
     *
     * 数据库字段信息:notifi_type TINYINT(3)
     */
    private String notifyType;

    /**
     * 字段名称：通知用户
     *
     * 数据库字段信息:notify_users VARCHAR(255)
     */
    private String notifyUsers;

    /**
     * 字段名称：生效状态
     */
    private String effectiveState;

}
