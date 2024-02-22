package com.dpline.remote.command;

import com.dpline.common.util.JSONUtils;
import lombok.Data;

@Data
public abstract class AbstractOperatorCommand {

    CommandType commandType;

    String traceId;

    /**
     * package response command
     *
     * @return command
     */
    public Command convert2Command() {
        Command command = new Command();
        command.setType(commandType);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }

}
