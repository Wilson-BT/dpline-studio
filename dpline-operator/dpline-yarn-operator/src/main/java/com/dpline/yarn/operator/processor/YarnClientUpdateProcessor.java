package com.dpline.yarn.operator.processor;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.YarnClusterParams;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.command.ClientUpdateCommand;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.command.ClientUpdateResponseCommand;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.yarn.operator.HadoopManager;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class YarnClientUpdateProcessor implements NettyRequestProcessor {


    @Autowired
    HadoopManager hadoopManager;


    private static final Logger logger = LoggerFactory.getLogger(YarnClientUpdateProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.CLIENT_UPDATE_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        ClientUpdateCommand clientUpdateCommand = JSONUtils.parseObject(command.getBody(), ClientUpdateCommand.class);
        try {
            YarnClusterParams oldYarnClusterParams = JSONUtils.parseObject(clientUpdateCommand.getOldClusterParamsContent(), YarnClusterParams.class);
//            YarnClusterParams newYarnClusterParams = JSONUtils.parseObject(clientUpdateCommand.getNewClusterParamsContent(), YarnClusterParams.class);
            hadoopManager.updateHadoop(clientUpdateCommand.getClusterEntityId().toString(),oldYarnClusterParams.getHadoopConfDir());
            channel.writeAndFlush(
                    new ClientUpdateResponseCommand(
                            ResponseStatus.SUCCESS,
                            "success"
                    ).convert2Command(command.getOpaque()));
        } catch (Exception exception) {
            logger.error("Update Hadoop client error.", ExceptionUtil.exceptionToString(exception));
            channel.writeAndFlush(
                    new ClientUpdateResponseCommand(
                            ResponseStatus.FAIL,
                            "Create new client failed"
                    ).convert2Command(command.getOpaque()));
        }
        // K8sClientDelCommand

    }
}
