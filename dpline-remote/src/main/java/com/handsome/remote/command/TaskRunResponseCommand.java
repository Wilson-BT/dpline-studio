package com.handsome.remote.command;



import com.handsome.common.request.SubmitResponse;

import java.io.Serializable;

public class TaskRunResponseCommand extends AbstractOperatorCommand implements Serializable {

    SubmitResponse submitResponse;


    public TaskRunResponseCommand(SubmitResponse submitRequest) {
        this.submitResponse = submitRequest;
        this.commandType = CommandType.TASK_RUN_RESPONSE;
    }

    public SubmitResponse getSubmitRequest() {
        return submitResponse;
    }

    public void setSubmitRequest(SubmitResponse submitRequest) {
        this.submitResponse = submitRequest;
    }


    @Override
    public String toString() {
        return "TaskRunCommand{" +
            "submitResponse=" + submitResponse +
            '}';
    }
}
