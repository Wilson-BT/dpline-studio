package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRunCommand extends AbstractOperatorCommand implements Serializable {

    String clusterType;

    String submitRequest;

    public TaskRunCommand(String submitRequest,String clusterType,String traceId) {
        this.clusterType = clusterType;
        this.submitRequest = submitRequest;
        this.commandType = CommandType.TASK_RUN_REQUEST;
        this.traceId = traceId;
    }

    public TaskRunCommand() {
    }

    public String getSubmitRequest() {
        return submitRequest;
    }

    public void setSubmitRequest(String submitRequest) {
        this.submitRequest = submitRequest;
    }


    @Override
    public String toString() {
        return "TaskRunCommand{" +
            "commandType=" + commandType +
            ", traceId='" + traceId + '\'' +
            ", clusterType='" + clusterType + '\'' +
            ", submitRequest='" + submitRequest + '\'' +
            '}';
    }
}
