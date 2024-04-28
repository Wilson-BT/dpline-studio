package com.dpline.remote.command;

import com.dpline.common.request.FlinkTriggerRequest;

import java.io.Serializable;

public class TaskTriggerCommand extends AbstractOperatorCommand implements Serializable {

    FlinkTriggerRequest triggerRequest;

    String clusterType;


    public TaskTriggerCommand(FlinkTriggerRequest triggerRequest,String clusterType,String traceId) {
        this.clusterType = clusterType;
        this.triggerRequest = triggerRequest;
        this.commandType = CommandType.TASK_TRIGGER_REQUEST;
        this.traceId = traceId;
    }

    public TaskTriggerCommand() {
    }

    public FlinkTriggerRequest getTriggerRequest() {
        return triggerRequest;
    }

    public void setStopRequest(FlinkTriggerRequest triggerRequest) {
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
