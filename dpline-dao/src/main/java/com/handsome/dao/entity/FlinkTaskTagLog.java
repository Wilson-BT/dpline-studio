package com.handsome.dao.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.TaskType;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dpline_flink_task_tag_log")
public class FlinkTaskTagLog {

    /**
     * Flink task tag id
     */
    @TableId(value = "id")
    private long id;

    /**
     * task definition id
     */
    @TableField("task_definition_id")
    private long taskDefinitionId;

    /**
     * task name
     */
    private String tagName;

    /**
     * flink version id
     */
    private int flinkVersionId;

    /**
     * project id
     */
    private long projectCode;

    /**
     * task type:
     * Sql or custom code
     */
    private TaskType taskType;

    /**
     * sql if task is sql type，default null
     */
    private String sqlText;

    /**
     * jar path for custom code,
     * searched from project code to
     */
    private String mainJarPath;

    /**
     * main class name
     * // TODO 这里必须要兼容sql 中的模式
     */
    private String mainClassName;

    /**
     * other params for jar main class
     */
    private String classParams;

    /**
     * resource ids
     */
    private String resourceIds;

    /**
     * udf ids
     */
    private String udfIds;

    /**
     * task description
     */
    private String description;

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


}
