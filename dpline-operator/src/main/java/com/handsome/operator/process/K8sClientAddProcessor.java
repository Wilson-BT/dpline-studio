package com.handsome.operator.process;

import com.google.common.base.Preconditions;
import com.handsome.remote.command.Command;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

/**
 * 添加k8s的客户端
 */
@Component
public class K8sClientAddProcessor implements NettyRequestProcessor {


    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.K8S_CLIENT_ADD_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));


    }
}
