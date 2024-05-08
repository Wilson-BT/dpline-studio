package com.dpline.yarn.operator.processor;

import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskRunCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



/**
 * task submit
 */
@Component
public class TaskRunProcessor extends MDCEnvSideCar implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_RUN_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        TaskRunCommand taskRunCommand = JSONUtils.parseObject(command.getBody(), TaskRunCommand.class);

        String submitRequest = taskRunCommand.getSubmitRequest();
        if (Asserts.isNull(submitRequest)){
            return;
        }
        FlinkK8sRemoteSubmitRequest flinkSubmitRequest = JSONUtils.parseObject(submitRequest, FlinkK8sRemoteSubmitRequest.class);

    }
}
