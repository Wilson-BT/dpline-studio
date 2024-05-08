package com.dpline.k8s.operator.process;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.request.ClusterResponse;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.k8s.operator.k8s.K8sClientLoopList;
import com.dpline.k8s.operator.k8s.K8sClusterManager;
import com.dpline.k8s.operator.config.WatcherConfig;
import com.dpline.remote.command.*;
import com.google.common.base.Preconditions;
import com.dpline.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 添加k8s的客户端
 */
@Component
public class K8sClientAddProcessor implements NettyRequestProcessor {

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    WatcherConfig watcherConfig;

    private static Logger logger = LoggerFactory.getLogger(K8sClientAddProcessor.class);

    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.CLIENT_ADD_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        try{
            ClientAddCommand k8sClientAddCommand = JSONUtils.parseObject(command.getBody(), ClientAddCommand.class);
            K8sClusterParams k8sClusterParams = JSONUtils.parseObject(k8sClientAddCommand.getNewClusterParamsContent(), K8sClusterParams.class);
            logger.info("Create new Cluster {}",k8sClusterParams);
            if(Asserts.isNull(k8sClusterParams)){
                logger.error("K8sClusterParams is not exists.");
                channel.writeAndFlush(
                        new ClientAddResponseCommand(new ClusterResponse(ResponseStatus.FAIL))
                                .convert2Command(command.getOpaque()));
                return;
            }
            Optional<ConcurrentHashMap<Long, K8sClientLoopList>> k8sClient =
                k8sClusterManager.createK8sClient(
                        k8sClientAddCommand.getClusterEntityId(),
                        k8sClusterParams,
                        watcherConfig.getCacheK8sClientNum());
            if(!k8sClient.isPresent()){
                channel.writeAndFlush(
                    new ClientAddResponseCommand(new ClusterResponse(ResponseStatus.FAIL))
                        .convert2Command(command.getOpaque()));
                return;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.exceptionToString(e));
        }
        channel.writeAndFlush(
            new ClientAddResponseCommand(
                new ClusterResponse(ResponseStatus.SUCCESS)
            ).convert2Command(command.getOpaque())
        );
    }
}
