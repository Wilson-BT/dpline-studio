package com.dpline.operator.process;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.params.K8sClusterParams;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.JSONUtils;
import com.dpline.dao.mapper.ClusterMapper;
import com.dpline.operator.config.WatcherConfig;
import com.dpline.operator.k8s.K8sClientLoopList;
import com.dpline.operator.k8s.K8sClusterManager;
import com.dpline.remote.command.*;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class K8sClientUpdateProcessor implements NettyRequestProcessor {

    @Autowired
    K8sClusterManager k8sClusterManager;

    @Autowired
    ClusterMapper clusterMapper;

    @Autowired
    WatcherConfig watcherConfig;

    private static final Logger logger = LoggerFactory.getLogger(K8sClientUpdateProcessor.class);

    @Override
    public void process(Channel channel, Command command) throws Exception {
        Preconditions.checkArgument(CommandType.K8S_CLIENT_UPDATE_REQUEST == command.getType(), String.format("invalid command type: %s", command.getType()));
        try {
            K8sClientUpdateCommand k8sClientUpdateCommand = JSONUtils.parseObject(command.getBody(), K8sClientUpdateCommand.class);
            K8sClusterParams oldK8sClusterParams = JSONUtils.parseObject(k8sClientUpdateCommand.getOldClusterParamsContent(), K8sClusterParams.class);
            K8sClusterParams newK8sClusterParams = JSONUtils.parseObject(k8sClientUpdateCommand.getNewClusterParamsContent(), K8sClusterParams.class);
            Long clusterEntityId = k8sClientUpdateCommand.getClusterEntityId();
            // 如果两者一样的话，只更新 缓存配置，不更新客户端
            if(oldK8sClusterParams.getNameSpace().equals(newK8sClusterParams.getNameSpace()) &&
                    oldK8sClusterParams.getKubePath().equals(oldK8sClusterParams.getKubePath())
            ){
                logger.info("Namespace and kubePath is same as old cluster, just update cached K8sClusterParams");
                k8sClusterManager.updateK8sClusterIdConfigMap(clusterEntityId,newK8sClusterParams);
                channel.writeAndFlush(
                        new K8sClientUpdateResponseCommand(ResponseStatus.SUCCESS,"")
                                .convert2Command(command.getOpaque())
                );
                return;
            }
            // delete
            k8sClusterManager.deleteClientIfExist(clusterEntityId);
            // and recreate
            Optional<ConcurrentHashMap<Long, K8sClientLoopList>> k8sClient =
                    k8sClusterManager.createK8sClient(
                            clusterEntityId,
                            newK8sClusterParams,
                            watcherConfig.getCacheK8sClientNum());
            if(k8sClient.isPresent()){
                channel.writeAndFlush(new K8sClientUpdateResponseCommand(ResponseStatus.SUCCESS,"")
                        .convert2Command(command.getOpaque()));
                return;
            }
        } catch (Exception ex) {
            logger.error(ExceptionUtil.exceptionToString(ex));
        }
        // K8sClientDelCommand
        channel.writeAndFlush(
                new K8sClientUpdateResponseCommand(
                        ResponseStatus.FAIL,
                        "Create new client failed"
                ).convert2Command(command.getOpaque()));
    }
}
