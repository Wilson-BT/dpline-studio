package com.handsome.dao.dto;

import com.handsome.common.enums.TaskType;
import com.handsome.dao.entity.FlinkTaskDefinition;

import javax.validation.constraints.NotNull;
import java.util.Date;


public class FlinkTaskDefinitionDto {

    @NotNull(message = "flink task id")
    private Long id;

    /**
     * task name
     */
    @NotNull(message = "task name")
    private String taskName;

    /**
     * flink version id
     */
    @NotNull(message = "flink version id")
    private Integer flinkVersionId;

    /**
     * project id
     */
    @NotNull(message = "project code")
    private Long projectCode;

    /**
     * task type:
     * Sql or custom code
     */
    @NotNull(message = "task type")
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


    public int getFlinkVersionId() {
        return flinkVersionId;
    }

    public void setFlinkVersionId(int flinkVersionId) {
        this.flinkVersionId = flinkVersionId;
    }

    public long getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(long projectCode) {
        this.projectCode = projectCode;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getMainJarPath() {
        return mainJarPath;
    }

    public void setMainJarPath(String mainJarPath) {
        this.mainJarPath = mainJarPath;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassParams() {
        return classParams;
    }

    public void setClassParams(String classParams) {
        this.classParams = classParams;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FlinkTaskDefinition createFlinkTaskDefinition() {
        FlinkTaskDefinition flinkTaskDefinition = new FlinkTaskDefinition();
        flinkTaskDefinition.setId(this.getId());
        flinkTaskDefinition.setTaskName(this.getTaskName());
        flinkTaskDefinition.setTaskType(this.getTaskType());
        flinkTaskDefinition.setFlinkVersionId(this.getFlinkVersionId());
        flinkTaskDefinition.setDescription(this.getDescription());
        flinkTaskDefinition.setClassParams(this.getClassParams());
        flinkTaskDefinition.setProjectCode(this.getProjectCode());
        flinkTaskDefinition.setSqlText(this.getSqlText());
        flinkTaskDefinition.setMainClassName(this.getMainClassName());
        flinkTaskDefinition.setMainJarPath(this.getMainJarPath());
        flinkTaskDefinition.setUpdateTime(new Date());
        return flinkTaskDefinition;
    }
}
