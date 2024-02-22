package com.handsome.remote.command;

import com.handsome.common.request.StopRequest;
import com.handsome.common.request.TriggerRequest;

import java.io.Serializable;

public class TaskTriggerCommand extends AbstractOperatorCommand implements Serializable {

    TriggerRequest triggerRequest;


    public TaskTriggerCommand(TriggerRequest triggerRequest) {
        this.triggerRequest = triggerRequest;
        this.commandType = CommandType.TASK_TRIGGER_REQUEST;
    }

    public TriggerRequest getTriggerRequest() {
        return triggerRequest;
    }

    public void setStopRequest(TriggerRequest triggerRequest) {
        this.triggerRequest = triggerRequest;
    }

    @Override
    public String toString() {
        return "TaskTriggerCommand{" +
            "commandType=" + commandType +
            ", triggerRequest=" + triggerRequest +
            '}';
    }


}
