package com.dpline.k8s.operator.process;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkTriggerRequest;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.operator.processor.MDCEnvSideCar;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskTriggerCommand;
import com.dpline.remote.command.TaskTriggerResponseCommand;
import com.google.common.base.Preconditions;
import com.dpline.common.request.TriggerResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import com.dpline.k8s.operator.service.TaskOperateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskTriggerProcessor extends MDCEnvSideCar implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskTriggerProcessor.class);

    @Autowired
    TaskOperateService taskOperateService;

    @Override
    public void process(Channel channel, Command command) throws Exception {

        Preconditions.checkArgument(CommandType.TASK_TRIGGER_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));

        TaskTriggerCommand taskTriggerCommand = JSONUtils.parseObject(command.getBody(), TaskTriggerCommand.class);
        FlinkTriggerRequest triggerRequest = taskTriggerCommand.getTriggerRequest();
        if (Asserts.isNull(triggerRequest)) {
            return;
        }
        try {
            envInit(triggerRequest
                    .getJobId()
                    .toString(),
                taskTriggerCommand.getTraceId(),
                OperationsEnum.TRIGGER);
            logger.info("Request has been received,type:[{}], context:[{}]", command.getType(), triggerRequest);
            TriggerResponse triggerResponse = taskOperateService.trigger(triggerRequest);
            TaskTriggerResponseCommand taskTriggerResponseCommand = new TaskTriggerResponseCommand(triggerResponse);
            channel.writeAndFlush(taskTriggerResponseCommand.convert2Command(command.getOpaque()));
        } catch (Exception ex) {
            logger.error(ExceptionUtil.exceptionToString(ex));
        } finally {
            revertEnv();
        }
    }
}
