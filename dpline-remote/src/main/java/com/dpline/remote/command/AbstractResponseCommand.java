package com.dpline.remote.command;

import com.dpline.common.util.JSONUtils;

import java.io.Serializable;

public class AbstractResponseCommand implements Serializable {

    CommandType commandType;

    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(commandType);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }
}
