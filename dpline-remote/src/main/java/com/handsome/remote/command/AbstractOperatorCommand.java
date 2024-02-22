package com.handsome.remote.command;

import com.handsome.common.util.JSONUtils;

public class AbstractOperatorCommand {

    CommandType commandType;

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
