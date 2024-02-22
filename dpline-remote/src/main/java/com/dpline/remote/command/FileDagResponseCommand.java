package com.dpline.remote.command;

import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDagResponseCommand extends AbstractResponseCommand implements Serializable {

    FlinkDagResponse flinkDagResponse;

    public FileDagResponseCommand(FlinkDagResponse flinkDagResponse) {
        this.flinkDagResponse = flinkDagResponse;
        this.commandType = CommandType.FILE_DAG_RESPONSE;
    }

    public FileDagResponseCommand() {
    }


}
