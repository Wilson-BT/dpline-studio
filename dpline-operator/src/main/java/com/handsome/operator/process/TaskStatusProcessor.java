package com.handsome.operator.process;

import com.google.common.base.Preconditions;
import com.handsome.remote.command.Command;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskStatusProcessor implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_WATCH_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
    }
}
