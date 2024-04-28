package com.dpline.operator.process;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.K8sClusterResponse;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.operator.k8s.K8sClusterManager;
import com.dpline.remote.command.K8sClientDelCommand;
import com.dpline.remote.command.K8sClientDelResponseCommand;
import com.google.common.base.Preconditions;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class K8sClientRemoveProcessor implements NettyRequestProcessor {

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    ClusterMapper clusterMapper;

    private static final Logger logger = LoggerFactory.getLogger(K8sClientRemoveProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.K8S_CLIENT_REMOVE_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        // K8sClientDelCommand
        K8sClientDelCommand k8sClientDelCommand = JSONUtils.parseObject(command.getBody(), K8sClientDelCommand.class);
        logger.info("K8s remove client command had received.{}",k8sClientDelCommand);
        Long clusterId = k8sClientDelCommand.getClusterId();
        if(!k8sClusterManager.deleteClientIfExist(clusterId)){
            channel.writeAndFlush(new K8sClientDelResponseCommand(
                new K8sClusterResponse(ResponseStatus.FAIL))
                .convert2Command(command.getOpaque()));
            return;
        }
        channel.writeAndFlush(new K8sClientDelResponseCommand(new K8sClusterResponse(ResponseStatus.SUCCESS))
            .convert2Command(command.getOpaque()));
    }
}
