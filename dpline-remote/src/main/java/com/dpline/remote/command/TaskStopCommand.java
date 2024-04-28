package com.dpline.remote.command;

import com.dpline.common.request.FlinkStopRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskStopCommand extends AbstractOperatorCommand implements Serializable {

    FlinkStopRequest flinkStopRequest;

    String clusterType;

    public TaskStopCommand(){}

    public TaskStopCommand(FlinkStopRequest stopRequest,String clusterType,String traceId) {
        this.flinkStopRequest = stopRequest;
        this.clusterType = clusterType;
        this.commandType = CommandType.TASK_STOP_REQUEST;
        this.traceId = traceId;
    }
}
