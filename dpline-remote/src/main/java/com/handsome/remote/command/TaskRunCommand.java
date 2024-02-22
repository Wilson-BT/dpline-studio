package com.handsome.remote.command;

import com.handsome.common.request.SubmitRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRunCommand extends AbstractOperatorCommand implements Serializable {

    SubmitRequest submitRequest;

    public TaskRunCommand(SubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
        this.commandType = CommandType.TASK_RUN_REQUEST;
    }

    public TaskRunCommand() {
    }

    public SubmitRequest getSubmitRequest() {
        return submitRequest;
    }

    public void setSubmitRequest(SubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
    }

    @Override
    public String toString() {
        return "TaskRunCommand{" +
            "submitRequest=" + submitRequest +
            '}';
    }
}
