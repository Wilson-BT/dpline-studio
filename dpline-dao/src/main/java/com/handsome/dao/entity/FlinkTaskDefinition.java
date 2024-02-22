package com.handsome.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.handsome.common.enums.TaskType;
import com.handsome.common.util.CodeGenerateUtils;
import lombok.Data;

import java.util.Date;

/**
 * Flink 实例
 */
@Data
@TableName("dpline_flink_task_definition")
public class FlinkTaskDefinition {

    /**
     * Flink 定义的code
     */
    @TableId(value = "id")
    private long id;

    /**
     * task name
     */
    private String taskName;

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
     */
    private String mainClassName;

    /**
     * other params for jar main class
     */
    private String classParams;

    /**
     * task description
     */
    private String description;

    /**
     * resource ids ,join by ','
     */
    @TableField(exist = false)
    private String resourceIds;

    /**
     * udf ids, join by ','
     */
    @TableField(exist = false)
    private String udfIds;

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


    public FlinkTaskTagLog createFlinkTaskTagLog(String tagName) throws CodeGenerateUtils.CodeGenerateException {
        FlinkTaskTagLog flinkTaskTagLog = new FlinkTaskTagLog();
        flinkTaskTagLog.setId(CodeGenerateUtils.getInstance().genCode());
        flinkTaskTagLog.setTaskDefinitionId(this.getId());
        flinkTaskTagLog.setTagName(tagName);
        flinkTaskTagLog.setTaskType(this.getTaskType());
        flinkTaskTagLog.setFlinkVersionId(this.getFlinkVersionId());
        flinkTaskTagLog.setDescription(this.getDescription());
        flinkTaskTagLog.setClassParams(this.getClassParams());
        flinkTaskTagLog.setProjectCode(this.getProjectCode());
        flinkTaskTagLog.setSqlText(this.getSqlText());
        // if sql mainClass is dpline-app-main-class
        flinkTaskTagLog.setMainClassName(this.getMainClassName());
        // sql mode，main jar path is dpline-app.jar
        flinkTaskTagLog.setMainJarPath(this.getMainJarPath());
        flinkTaskTagLog.setCreateTime(new Date());
        flinkTaskTagLog.setUpdateTime(new Date());
        return flinkTaskTagLog;
    }
}