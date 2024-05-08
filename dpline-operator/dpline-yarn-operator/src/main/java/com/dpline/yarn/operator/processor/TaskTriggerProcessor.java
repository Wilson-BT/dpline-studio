package com.dpline.yarn.operator.processor;

import com.dpline.common.request.FlinkTriggerRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskTriggerCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskTriggerProcessor extends MDCEnvSideCar implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskTriggerProcessor.class);

    @Override
    public void process(Channel channel, Command command) throws Exception {

        Preconditions.checkArgument(CommandType.TASK_TRIGGER_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));

        TaskTriggerCommand taskTriggerCommand = JSONUtils.parseObject(command.getBody(), TaskTriggerCommand.class);
        FlinkTriggerRequest triggerRequest = taskTriggerCommand.getTriggerRequest();
        if (Asserts.isNull(triggerRequest)) {
            return;
        }
    }
}
