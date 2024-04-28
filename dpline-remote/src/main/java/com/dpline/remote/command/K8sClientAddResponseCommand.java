package com.dpline.remote.command;

import com.dpline.common.request.K8sClusterResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class K8sClientAddResponseCommand extends AbstractResponseCommand {

    K8sClusterResponse k8sClusterResponse;

    public K8sClientAddResponseCommand(K8sClusterResponse k8sClusterResponse) {
        this.k8sClusterResponse = k8sClusterResponse;
        this.commandType = CommandType.K8S_CLIENT_ADD_RESPONSE;
    }

    public K8sClientAddResponseCommand() {
    }

    /**
     * package response command
     *
     * @return command
     */
}
