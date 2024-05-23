package com.dpline.yarn.operator.processor;

import com.dpline.common.enums.ClusterType;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.request.SubmitResponse;
import com.dpline.common.request.YarnRemoteSubmitRequest;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.processor.MDCEnvSideCar;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskRunCommand;
import com.dpline.remote.command.TaskRunResponseCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.yarn.operator.service.TaskOperatorService;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * task submit
 */
@Component
public class TaskRunProcessor extends MDCEnvSideCar implements NettyRequestProcessor {

    private Logger logger = LoggerFactory.getLogger(TaskRunProcessor.class);

    @Autowired
    TaskOperatorService taskOperatorService;
    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_RUN_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        TaskRunCommand taskRunCommand = JSONUtils.parseObject(command.getBody(), TaskRunCommand.class);
        String submitRequest = taskRunCommand.getSubmitRequest();
        if (Asserts.isNull(submitRequest)){
            return;
        }
        YarnRemoteSubmitRequest flinkSubmitRequest = JSONUtils.parseObject(submitRequest, YarnRemoteSubmitRequest.class);

        envInit(flinkSubmitRequest
                        .getJobDefinitionOptions()
                        .getJobId()
                        .toString(),
                taskRunCommand.getTraceId(),
                OperationsEnum.START);
        logger.info("Request has been received,type:[{}], context:[{}]",command.getType(), flinkSubmitRequest);
        SubmitResponse submitResponse;
        try {
            submitResponse = taskOperatorService.submitAndWatch(flinkSubmitRequest);
            TaskRunResponseCommand taskRunResponseCommand = new TaskRunResponseCommand(submitResponse);
            channel.writeAndFlush(taskRunResponseCommand.convert2Command(command.getOpaque()));
        } catch (Exception ex){
            logger.error(ExceptionUtil.exceptionToString(ex));
        } finally {
            revertEnv();
        }
    }
}
