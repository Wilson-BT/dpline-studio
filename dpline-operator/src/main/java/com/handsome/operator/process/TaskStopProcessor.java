package com.handsome.operator.process;

import com.google.common.base.Preconditions;
import com.handsome.common.request.StopRequest;
import com.handsome.common.request.StopResponse;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.JSONUtils;
import com.handsome.operator.service.TaskOperateService;
import com.handsome.remote.command.*;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStopProcessor implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Autowired
    TaskOperateService taskOperateService;

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_STOP_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));

        try {
            TaskStopCommand taskStopCommand = JSONUtils.parseObject(command.getBody(), TaskStopCommand.class);
            StopRequest stopRequest = taskStopCommand.getStopRequest();
            if (Asserts.isNull(stopRequest)){
                return;
            }
            logger.info("Request has been received,type:[{}], context:[{}]",command.getType(),stopRequest);
            StopResponse stopResponse = taskOperateService.stop(stopRequest);
            TaskStopResponseCommand taskStopResponseCommand = new TaskStopResponseCommand(stopResponse);
            channel.writeAndFlush(taskStopResponseCommand);
        } catch (Exception exception) {
            logger.error("Task stop Failed.");
            exception.printStackTrace();
        }


    }

}
