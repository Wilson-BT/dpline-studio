package com.dpline.remote.command;

import com.dpline.common.request.StopResponse;
import com.dpline.common.util.JSONUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务停止的Response
 */
@Data
public class TaskStopResponseCommand extends AbstractResponseCommand implements Serializable {

    StopResponse stopResponse;


    public TaskStopResponseCommand() {
        this.commandType = CommandType.TASK_STOP_RESPONSE;
    }

    public Command convert2Command(long opaque) {
        Command command = new Command();
        command.setType(commandType);
        command.setOpaque(opaque);
        byte[] body = JSONUtils.toJsonByteArray(this);
        command.setBody(body);
        return command;
    }


    public static void main(String[] args) {
        String json="{\"stopResponse\":{\"clusterId\":\"new_ods_new_k8s\",\"jobId\":10358341329440,\"responseStatus\":\"FAIL\",\"savePointAddress\":null}}";
        TaskStopResponseCommand taskStopResponseCommand = JSONUtils.parseObject(json, TaskStopResponseCommand.class);
        System.out.println(taskStopResponseCommand.toString());
    }
}
