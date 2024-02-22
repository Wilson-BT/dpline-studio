package com.handsome.remote.command;

import com.handsome.common.request.StopResponse;
import com.handsome.common.request.TriggerResponse;
import com.handsome.common.util.JSONUtils;

import java.io.Serializable;

public class TaskTriggerResponseCommand extends AbstractOperatorCommand implements Serializable {

    TriggerResponse triggerResponse;

    public TaskTriggerResponseCommand(TriggerResponse triggerResponse) {
        this.triggerResponse = triggerResponse;
    }

    public TriggerResponse getTriggerResponse() {
        return triggerResponse;
    }

    public void setTriggerResponse(TriggerResponse triggerResponse) {
        this.triggerResponse = triggerResponse;
    }

    /**
     *  package request command
     *
     * @return command
     */
    public Command convert2Command() {
        Command command = new Command();
        command.setType(CommandType.TASK_TRIGGER_RESPONSE);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }

}
