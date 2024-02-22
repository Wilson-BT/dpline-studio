package com.handsome.remote.command;

import com.handsome.common.util.JSONUtils;

import java.io.Serializable;

public class K8sClientAddResponseCommand implements Serializable {



    /**
     * package response command
     *
     * @return command
     */
    public Command convert2Command() {
        Command command = new Command();
        command.setType(CommandType.K8S_CLIENT_ADD_REQUEST);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }
}
