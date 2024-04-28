package com.dpline.operator.process;

import com.dpline.common.request.TaskAlertEditRequest;
import com.dpline.common.request.TaskAlertEditResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.service.TaskClusterMapService;
import com.dpline.remote.command.*;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskAlertEditProcessor implements NettyRequestProcessor {

    @Autowired
    TaskClusterMapService taskClusterMapService;

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
        TaskAlertEditResponse taskAlertEditResponse = taskClusterMapService.updateTaskAlert(taskAlertEditRequest);
        TaskAlertEditResponseCommand taskAlertEditResponseCommand = new TaskAlertEditResponseCommand(taskAlertEditResponse);
        channel.writeAndFlush(taskAlertEditResponseCommand.convert2Command(command.getOpaque()));
    }
}
