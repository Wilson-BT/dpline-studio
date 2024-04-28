package com.dpline.remote.command;

import com.dpline.common.request.TaskAlertEditRequest;
import lombok.Data;

@Data
public class TaskAlertEditCommand extends AbstractOperatorCommand {

    private TaskAlertEditRequest taskAlertEditRequest;

    public TaskAlertEditCommand(){}

    public TaskAlertEditCommand(TaskAlertEditRequest taskAlertEditRequest) {
        this.taskAlertEditRequest = taskAlertEditRequest;
        this.commandType = CommandType.TASK_ALERT_EDIT_REQUEST;
    }
}
