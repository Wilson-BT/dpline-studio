package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class K8sClientUpdateCommand extends AbstractOperatorCommand implements Serializable {

    Long clusterEntityId;

    String newClusterParamsContent;

    String oldClusterParamsContent;

    public K8sClientUpdateCommand(Long clusterEntityId,
                                  String oldClusterParamsContent,
                                  String newClusterParamsContent) {
        this.clusterEntityId = clusterEntityId;
        this.oldClusterParamsContent = oldClusterParamsContent;
        this.newClusterParamsContent = newClusterParamsContent;
        this.commandType=CommandType.K8S_CLIENT_UPDATE_REQUEST;
    }

    public K8sClientUpdateCommand() {
    }
}
