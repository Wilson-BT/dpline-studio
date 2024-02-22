package com.dpline.remote.command;

import java.io.Serializable;

public class TestCommandReq extends AbstractOperatorCommand implements Serializable {

    public TestCommandReq(){
        this.commandType = CommandType.TEST_REQ;
    }
}
