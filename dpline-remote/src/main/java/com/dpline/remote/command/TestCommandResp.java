package com.dpline.remote.command;

import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class TestCommandResp extends AbstractOperatorCommand implements Serializable {

    public TestCommandResp(){
        this.commandType = CommandType.TEST_RESP;
    }

    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(commandType);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }
}
