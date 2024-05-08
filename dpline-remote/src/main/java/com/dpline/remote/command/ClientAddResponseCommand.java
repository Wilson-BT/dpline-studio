package com.dpline.remote.command;

import com.dpline.common.request.ClusterResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientAddResponseCommand extends AbstractResponseCommand {

    ClusterResponse clusterResponse;

    public ClientAddResponseCommand(ClusterResponse clusterResponse) {
        this.clusterResponse = clusterResponse;
        this.commandType = CommandType.CLIENT_ADD_RESPONSE;
    }

    public ClientAddResponseCommand() {
    }

    /**
     * package response command
     *
     * @return command
     */
}
