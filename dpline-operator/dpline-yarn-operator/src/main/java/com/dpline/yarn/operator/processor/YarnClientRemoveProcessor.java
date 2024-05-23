package com.dpline.yarn.operator.processor;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.ClusterResponse;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.command.ClientDelCommand;
import com.dpline.remote.command.ClientDelResponseCommand;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.yarn.operator.HadoopManager;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YarnClientRemoveProcessor implements NettyRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(YarnClientRemoveProcessor.class);

    @Autowired
    HadoopManager hadoopManager;

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.CLIENT_REMOVE_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        // YarnClientDelCommand
        ClientDelCommand clientDelCommand = JSONUtils.parseObject(command.getBody(), ClientDelCommand.class);
        logger.info("yarn remove client command had received.{}",clientDelCommand);
        try {
            hadoopManager.closeHadoop(clientDelCommand.getClusterId());
            channel.writeAndFlush(new ClientDelResponseCommand(new ClusterResponse(ResponseStatus.SUCCESS))
                    .convert2Command(command.getOpaque()));
        } catch (Exception exception){
            channel.writeAndFlush(new ClientDelResponseCommand(new ClusterResponse(ResponseStatus.FAIL))
                    .convert2Command(command.getOpaque()));
        }

    }
}
