package com.dpline.remote.command;

import com.dpline.common.enums.ResponseStatus;
import lombok.Data;

@Data
public class ClientUpdateResponseCommand extends AbstractResponseCommand {

    ResponseStatus responseStatus;

    String msg;

    public ClientUpdateResponseCommand(ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.msg = msg;
        this.commandType = CommandType.CLIENT_UPDATE_RESPONSE;
    }

    public ClientUpdateResponseCommand() {
    }
}
