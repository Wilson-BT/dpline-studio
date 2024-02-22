package com.dpline.remote.command;

import com.dpline.common.request.K8sClusterResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class K8sClientDelResponseCommand extends AbstractResponseCommand {

    K8sClusterResponse k8sClusterResponse;


    public K8sClientDelResponseCommand() {
    }

    public K8sClientDelResponseCommand(K8sClusterResponse k8sClusterResponse) {
        this.k8sClusterResponse = k8sClusterResponse;
        this.commandType = CommandType.K8S_CLIENT_REMOVE_RESPONSE;
    }
}
