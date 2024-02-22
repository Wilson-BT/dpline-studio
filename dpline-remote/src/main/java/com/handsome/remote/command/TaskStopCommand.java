package com.handsome.remote.command;

import com.handsome.common.request.StopRequest;

import java.io.Serializable;

public class TaskStopCommand extends AbstractOperatorCommand implements Serializable {

    StopRequest stopRequest;


    public TaskStopCommand(StopRequest stopRequest) {
        this.stopRequest = stopRequest;
        this.commandType = CommandType.TASK_STOP_REQUEST;
    }

    public StopRequest getStopRequest() {
        return stopRequest;
    }

    public void setStopRequest(StopRequest stopRequest) {
        this.stopRequest = stopRequest;
    }

    @Override
    public String toString() {
        return "TaskStopCommand{" +
            "commandType=" + commandType +
            ", stopRequest=" + stopRequest +
            '}';
    }
}
