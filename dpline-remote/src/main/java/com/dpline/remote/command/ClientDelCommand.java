package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientDelCommand extends AbstractOperatorCommand implements Serializable {

    Long clusterId;

    public ClientDelCommand(Long clusterId) {
        this.clusterId = clusterId;
        this.commandType = CommandType.CLIENT_REMOVE_REQUEST;
    }

    public ClientDelCommand() {
    }

//    public Command convert2Command() {
//        Command command = new Command();
//        command.setType(this.commandType);
//        byte[] body = JSONUtils.toJsonByteArray(this);
//        command.setBody(body);
//        return command;
//    }
}
