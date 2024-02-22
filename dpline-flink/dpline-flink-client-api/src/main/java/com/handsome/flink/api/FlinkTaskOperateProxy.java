package com.handsome.flink.api;

import com.handsome.common.request.*;
import com.handsome.flink.core.stop.TaskStop;
import com.handsome.flink.core.stop.TaskStopper;
import com.handsome.flink.core.submit.TaskSubmit;
import com.handsome.flink.core.submit.TaskSubmitter;

import java.io.IOException;

public class FlinkTaskOperateProxy implements TaskSubmit, TaskStop {

    private final static TaskSubmitter taskSubmitter = new TaskSubmitter();

    private final static TaskStopper taskStopper = new TaskStopper();

    @Override
    public TriggerResponse trigger(TriggerRequest triggerRequest) throws IOException {
        return taskStopper.triggerSavePoint(triggerRequest);
    }

    @Override
    public StopResponse stop(StopRequest stopRequest) throws Exception {
        return taskStopper.stop(stopRequest);
    }

    @Override
    public SubmitResponse submit(SubmitRequest submitRequest) throws Exception {
        return taskSubmitter.doSubmit(submitRequest);
    }
}
