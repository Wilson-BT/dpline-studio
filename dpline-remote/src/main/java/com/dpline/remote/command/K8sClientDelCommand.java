package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class K8sClientDelCommand extends AbstractOperatorCommand implements Serializable {

    Long clusterId;

    public K8sClientDelCommand(Long clusterId) {
        this.clusterId = clusterId;
        this.commandType = CommandType.K8S_CLIENT_REMOVE_REQUEST;
    }

    public K8sClientDelCommand() {
    }

//    public Command convert2Command() {
//        Command command = new Command();
//        command.setType(this.commandType);
//        byte[] body = JSONUtils.toJsonByteArray(this);
//        command.setBody(body);
//        return command;
//    }
}
