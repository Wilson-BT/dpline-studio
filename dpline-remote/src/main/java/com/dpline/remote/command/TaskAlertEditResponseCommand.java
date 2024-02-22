package com.dpline.remote.command;

import com.dpline.common.request.TaskAlertEditResponse;
import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskAlertEditResponseCommand extends AbstractResponseCommand implements Serializable {

    TaskAlertEditResponse taskAlertEditResponse;

    public TaskAlertEditResponseCommand(TaskAlertEditResponse taskAlertEditResponse) {
        this.taskAlertEditResponse = taskAlertEditResponse;
        this.commandType = CommandType.TASK_ALERT_EDIT_RESPONSE;
    }

    public TaskAlertEditResponseCommand() {
        this.commandType = CommandType.TASK_ALERT_EDIT_RESPONSE;
    }

    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(commandType);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }



}
