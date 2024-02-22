package com.handsome.operator.process;

import com.google.common.base.Preconditions;
import com.handsome.common.request.TriggerRequest;
import com.handsome.common.request.TriggerResponse;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.JSONUtils;
import com.handsome.remote.command.*;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import com.handsome.operator.service.TaskOperateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskTriggerProcessor implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Autowired
    TaskOperateService taskOperateService;

    @Override
    public void process(Channel channel, Command command) throws Exception {

        Preconditions.checkArgument(CommandType.TASK_TRIGGER_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));

        try {
            TaskTriggerCommand taskTriggerCommand = JSONUtils.parseObject(command.getBody(), TaskTriggerCommand.class);
            TriggerRequest triggerRequest = taskTriggerCommand.getTriggerRequest();
            if (Asserts.isNull(triggerRequest)){
                return;
            }
            logger.info("Request has been received,type:[{}], context:[{}]",command.getType(),triggerRequest);

            TriggerResponse triggerResponse = taskOperateService.trigger(triggerRequest);
            TaskTriggerResponseCommand taskTriggerResponseCommand = new TaskTriggerResponseCommand(triggerResponse);
            channel.writeAndFlush(taskTriggerResponseCommand);
        } catch (Exception exception) {
            logger.error("Task trigger Failed.");
            exception.printStackTrace();
        }

    }
}
