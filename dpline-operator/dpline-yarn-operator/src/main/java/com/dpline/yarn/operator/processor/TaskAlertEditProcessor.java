package com.dpline.yarn.operator.processor;

import com.dpline.common.request.TaskAlertEditRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskAlertEditCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskAlertEditProcessor implements NettyRequestProcessor {


    private Logger logger = LoggerFactory.getLogger(TaskAlertEditProcessor.class);

    @Override
    public void process(Channel channel, Command command) throws Exception {
        Preconditions.checkArgument(CommandType.TASK_ALERT_EDIT_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        TaskAlertEditCommand taskAlertEditCommand = JSONUtils.parseObject(command.getBody(), TaskAlertEditCommand.class);
        TaskAlertEditRequest taskAlertEditRequest = taskAlertEditCommand.getTaskAlertEditRequest();
        if (Asserts.isNull(taskAlertEditRequest)){
            return;
        }
        logger.info("Request has been received,type:[{}], context:[{}]",command.getType(),taskAlertEditRequest);
    }
}
