package com.dpline.remote.command;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class K8sClientUpdateResponseCommand extends AbstractResponseCommand {

    ResponseStatus responseStatus;

    String msg;

    public K8sClientUpdateResponseCommand(ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.msg = msg;
        this.commandType = CommandType.K8S_CLIENT_UPDATE_RESPONSE;
    }

    public K8sClientUpdateResponseCommand() {
    }
}
