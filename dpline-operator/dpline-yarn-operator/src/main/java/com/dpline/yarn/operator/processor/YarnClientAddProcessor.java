package com.dpline.yarn.operator.processor;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.YarnClusterParams;
import com.dpline.common.request.ClusterResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.remote.command.ClientAddCommand;
import com.dpline.remote.command.ClientAddResponseCommand;
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

import java.io.IOException;


/**
 * 添加k8s的客户端
 */
@Component
public class YarnClientAddProcessor implements NettyRequestProcessor {

    @Autowired
    ClusterMapper clusterMapper;

    @Autowired
    HadoopManager hadoopManager;

    private static Logger logger = LoggerFactory.getLogger(YarnClientAddProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.CLIENT_ADD_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        ClientAddCommand clientAddCommand = JSONUtils.parseObject(command.getBody(), ClientAddCommand.class);
        YarnClusterParams clusterParams = JSONUtils.parseObject(clientAddCommand.getNewClusterParamsContent(), YarnClusterParams.class);
        logger.info("Create new Cluster {}", clusterParams);
        if(Asserts.isNull(clusterParams)){
            logger.error("K8sClusterParams is not exists.");
            channel.writeAndFlush(
                    new ClientAddResponseCommand(new ClusterResponse(ResponseStatus.FAIL))
                            .convert2Command(command.getOpaque()));
            return;
        }
        try {
            hadoopManager.createHadoop(clientAddCommand.getClusterEntityId(),clusterParams.getHadoopHome());
            channel.writeAndFlush(
                    new ClientAddResponseCommand(new ClusterResponse(ResponseStatus.SUCCESS))
                            .convert2Command(command.getOpaque()));
        } catch (Exception e) {
            logger.error("Create new Cluster {} error", ExceptionUtil.exceptionToString(e));
            ClusterResponse clusterResponse = new ClusterResponse(ResponseStatus.FAIL);
            clusterResponse.setMsg("Create cluster Error.");
            channel.writeAndFlush(new ClientAddResponseCommand()
                    .convert2Command(command.getOpaque())
            );
        }
    }
}
