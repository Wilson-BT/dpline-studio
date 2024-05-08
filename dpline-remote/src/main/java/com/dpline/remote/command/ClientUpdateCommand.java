package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientUpdateCommand extends AbstractOperatorCommand implements Serializable {

    Long clusterEntityId;

    String newClusterParamsContent;

    String oldClusterParamsContent;

    public ClientUpdateCommand(Long clusterEntityId,
                                  String oldClusterParamsContent,
                                  String newClusterParamsContent) {
        this.clusterEntityId = clusterEntityId;
        this.oldClusterParamsContent = oldClusterParamsContent;
        this.newClusterParamsContent = newClusterParamsContent;
        this.commandType=CommandType.CLIENT_UPDATE_REQUEST;
    }

    public ClientUpdateCommand() {
    }
}
