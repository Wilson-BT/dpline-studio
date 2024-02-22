package com.dpline.remote.command;

import com.dpline.common.request.FlinkDagRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDagCommand extends AbstractOperatorCommand implements Serializable {

    FlinkDagRequest flinkDagRequest;

    public FileDagCommand(FlinkDagRequest flinkDagRequest) {
        this.flinkDagRequest = flinkDagRequest;
        this.commandType = CommandType.FILE_DAG_REQUEST;
    }

    public FileDagCommand() {
    }
}
