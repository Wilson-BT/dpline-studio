package com.handsome.remote.command;

import com.handsome.common.request.StopResponse;
import com.handsome.common.util.JSONUtils;

import java.io.Serializable;

/**
 * 任务停止的Response
 */
public class TaskStopResponseCommand extends AbstractOperatorCommand implements Serializable {

    StopResponse stopResponse;

    public TaskStopResponseCommand(StopResponse stopResponse) {
        this.stopResponse = stopResponse;
    }

    /**
     *  package request command
     *
     * @return command
     */
    public Command convert2Command() {
        Command command = new Command();
        command.setType(CommandType.TASK_STOP_RESPONSE);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }

    public StopResponse getStopResponse() {
        return stopResponse;
    }

    public void setStopResponse(StopResponse stopResponse) {
        this.stopResponse = stopResponse;
    }
}
