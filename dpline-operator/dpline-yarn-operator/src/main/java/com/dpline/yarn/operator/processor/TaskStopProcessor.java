package com.dpline.yarn.operator.processor;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkStopRequest;
import com.dpline.common.request.StopResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.processor.MDCEnvSideCar;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskStopCommand;
import com.dpline.remote.command.TaskStopResponseCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.yarn.operator.service.TaskOperatorService;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStopProcessor extends MDCEnvSideCar implements NettyRequestProcessor {

    @Autowired
    TaskOperatorService taskOperatorService;

    private Logger logger = LoggerFactory.getLogger(TaskStopProcessor.class);


    @Override
    public void process(Channel channel, Command command) throws Exception {
        logger.info("Received command [{}].",command);
        Preconditions.checkArgument(CommandType.TASK_STOP_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        TaskStopCommand taskStopCommand = JSONUtils.parseObject(command.getBody(), TaskStopCommand.class);
        FlinkStopRequest stopRequest = taskStopCommand.getFlinkStopRequest();
        if (Asserts.isNull(stopRequest)){
            return;
        }
        envInit(stopRequest
                .getJobId()
                .toString(),
            taskStopCommand.getTraceId(),
            OperationsEnum.STOP);
        logger.info("Request has been received,type:[{}], context:[{}]",command.getType(),stopRequest);

        try {
            StopResponse stopResponse = taskOperatorService.stop(stopRequest);
            TaskStopResponseCommand taskStopResponseCommand = new TaskStopResponseCommand();
            taskStopResponseCommand.setStopResponse(stopResponse);
            channel.writeAndFlush(taskStopResponseCommand.convert2Command(command.getOpaque()));
        }catch (Exception ex){
            logger.error(ExceptionUtil.exceptionToString(ex));
        } finally {
            revertEnv();
        }

    }

}
