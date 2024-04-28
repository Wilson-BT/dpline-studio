package com.dpline.remote.command;

import com.dpline.common.request.SubmitResponse;
import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskRunResponseCommand extends AbstractResponseCommand implements Serializable {

    SubmitResponse submitResponse;


    public TaskRunResponseCommand(SubmitResponse submitResponse) {
        this.submitResponse = submitResponse;
        this.commandType = CommandType.TASK_RUN_RESPONSE;
    }

    public TaskRunResponseCommand() {
    }

    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(commandType);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }
}
