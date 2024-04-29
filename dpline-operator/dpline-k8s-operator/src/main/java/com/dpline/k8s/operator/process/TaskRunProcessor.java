package com.dpline.k8s.operator.process;

import com.dpline.common.enums.ClusterType;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import com.dpline.common.util.ExceptionUtil;
import com.google.common.base.Preconditions;
import com.dpline.common.request.SubmitResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.k8s.operator.service.TaskOperateService;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.TaskRunCommand;
import com.dpline.remote.command.TaskRunResponseCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
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
    TaskOperateService taskOperateService;

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_RUN_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        TaskRunCommand taskRunCommand = JSONUtils.parseObject(command.getBody(), TaskRunCommand.class);

        String submitRequest = taskRunCommand.getSubmitRequest();
        if (Asserts.isNull(submitRequest)){
            return;
        }
        FlinkK8sRemoteSubmitRequest flinkSubmitRequest = JSONUtils.parseObject(submitRequest, FlinkK8sRemoteSubmitRequest.class);
        if(Asserts.isNull(flinkSubmitRequest)){
            return;
        }
        try {
            // 环境初始化
            envInit(flinkSubmitRequest
                    .getJobDefinitionOptions()
                    .getJobId()
                    .toString(),
                taskRunCommand.getTraceId(),
                OperationsEnum.START);
            logger.info("Request has been received,type:[{}], context:[{}]",command.getType(), flinkSubmitRequest);
            SubmitResponse submitResponse = null;

            if(!ClusterType.LOCAL.getValue().equals(taskRunCommand.getClusterType())){
                submitResponse = taskOperateService.submitAndWatch(flinkSubmitRequest);
            }

            TaskRunResponseCommand taskRunResponseCommand = new TaskRunResponseCommand(submitResponse);
            channel.writeAndFlush(taskRunResponseCommand.convert2Command(command.getOpaque()));
        }catch (Exception ex){
            logger.error(ExceptionUtil.exceptionToString(ex));
        } finally {
            revertEnv();
        }
    }
}
