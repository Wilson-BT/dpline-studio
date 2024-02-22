package com.dpline.remote.command;

import com.dpline.common.request.TriggerResponse;
import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaskTriggerResponseCommand extends AbstractResponseCommand implements Serializable {

    TriggerResponse triggerResponse;

    public TaskTriggerResponseCommand(TriggerResponse triggerResponse) {
        this.triggerResponse = triggerResponse;
    }

    public TriggerResponse getTriggerResponse() {
        return triggerResponse;
    }

    public TaskTriggerResponseCommand() {
    }


    public void setTriggerResponse(TriggerResponse triggerResponse) {
        this.triggerResponse = triggerResponse;
    }

    /**
     *  package request command
     *
     * @return command
     */
    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(CommandType.TASK_TRIGGER_RESPONSE);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }

}
