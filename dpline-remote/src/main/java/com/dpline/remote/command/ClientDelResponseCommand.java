package com.dpline.remote.command;

import com.dpline.common.request.ClusterResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientDelResponseCommand extends AbstractResponseCommand {

    ClusterResponse clusterResponse;


    public ClientDelResponseCommand() {
    }

    public ClientDelResponseCommand(ClusterResponse clusterResponse) {
        this.clusterResponse = clusterResponse;
        this.commandType = CommandType.CLIENT_REMOVE_RESPONSE;
    }
}
