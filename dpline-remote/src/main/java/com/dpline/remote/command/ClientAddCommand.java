package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientAddCommand extends AbstractOperatorCommand implements Serializable {

    String newClusterParamsContent;

    Long clusterEntityId;

    public ClientAddCommand(Long clusterEntityId,String newClusterParamsContent) {
        this.clusterEntityId = clusterEntityId;
        this.newClusterParamsContent = newClusterParamsContent;
        this.commandType=CommandType.CLIENT_ADD_REQUEST;
    }

    public ClientAddCommand() {
    }

}
