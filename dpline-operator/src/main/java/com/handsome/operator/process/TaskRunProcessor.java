package com.handsome.operator.process;

import com.google.common.base.Preconditions;
import com.handsome.common.request.SubmitRequest;
import com.handsome.common.request.SubmitResponse;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.JSONUtils;
import com.handsome.operator.service.TaskOperateService;
import com.handsome.remote.command.Command;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.command.TaskRunCommand;
import com.handsome.remote.command.TaskRunResponseCommand;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * task submit
 */
@Component
public class TaskRunProcessor implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Autowired
    TaskOperateService taskOperateService;

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_RUN_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        try {
            TaskRunCommand taskRunCommand = JSONUtils.parseObject(command.getBody(), TaskRunCommand.class);
            SubmitRequest submitRequest = taskRunCommand.getSubmitRequest();
            if (Asserts.isNull(submitRequest)){
                return;
            }
            logger.info("Request has been received,type:[{}], context:[{}]",command.getType(),submitRequest);

            SubmitResponse submitResponse = taskOperateService.submitAndWatch(submitRequest);
            TaskRunResponseCommand taskRunResponseCommand = new TaskRunResponseCommand(submitResponse);
            channel.writeAndFlush(taskRunResponseCommand);
        } catch (Exception exception) {
            logger.error("Submit and add to watch Failed.");
            exception.printStackTrace();
        }
    }
}
