package com.handsome.alert.api;

import com.handsome.common.enums.AlertType;

public class AlertEntity {

    private AlertType alertType;

    private int alterInstanceId;

    private String taskName;

    private  String taskStatusBefore;

    private  String taskStatusCurrent;

    public AlertEntity(AlertType alertType, int alterInstanceId, String taskName, String taskStatusBefore, String taskStatusCurrent) {
        this.alertType = alertType;
        this.alterInstanceId = alterInstanceId;
        this.taskName = taskName;
        this.taskStatusBefore = taskStatusBefore;
        this.taskStatusCurrent = taskStatusCurrent;
    }

    public AlertType getAlterType() {
        return alertType;
    }

    public void setWarningType(AlertType warningType) {
        this.alertType = warningType;
    }

    public int getAlterInstanceId() {
        return alterInstanceId;
    }

    public void setAlterInstanceId(int alterInstanceId) {
        this.alterInstanceId = alterInstanceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setAlterType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getTaskStatusBefore() {
        return taskStatusBefore;
    }

    public void setTaskStatusBefore(String taskStatusBefore) {
        this.taskStatusBefore = taskStatusBefore;
    }

    public String getTaskStatusCurrent() {
        return taskStatusCurrent;
    }

    public void setTaskStatusCurrent(String taskStatusCurrent) {
        this.taskStatusCurrent = taskStatusCurrent;
    }


}
