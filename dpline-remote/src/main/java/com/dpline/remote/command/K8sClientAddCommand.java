package com.dpline.remote.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class K8sClientAddCommand extends AbstractOperatorCommand implements Serializable {

    String newClusterParamsContent;

    Long clusterEntityId;

    public K8sClientAddCommand(Long clusterEntityId,String newClusterParamsContent) {
        this.clusterEntityId = clusterEntityId;
        this.newClusterParamsContent = newClusterParamsContent;
        this.commandType=CommandType.K8S_CLIENT_ADD_REQUEST;
    }

    public K8sClientAddCommand() {
    }

}
