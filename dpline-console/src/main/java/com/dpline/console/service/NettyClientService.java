package com.dpline.console.service;

import com.dpline.common.enums.ClusterType;
import com.dpline.common.util.JSONUtils;
import com.dpline.remote.NettyRemoteClient;
import com.dpline.remote.command.AbstractOperatorCommand;
import com.dpline.remote.command.AbstractResponseCommand;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.Host;
import com.dpline.remote.config.NettyClientConfig;
import com.dpline.remote.config.NettyServerConfig;
import com.dpline.remote.future.InvokeCallback;
import com.dpline.remote.future.ResponseFuture;
import com.dpline.remote.util.RpcAddress;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * netty client
 */
@Data
@Component
public class NettyClientService {

    private Logger logger = LoggerFactory.getLogger(NettyClientService.class);

    private ConcurrentHashMap<String, RpcAddress> rpcAddressMap = new ConcurrentHashMap<String, RpcAddress>();

    private NettyClientConfig clientConfig;

    private NettyServerConfig serverConfig;

    private NettyRemoteClient client;

    private boolean isRunning;

    /**
     * request time out
     */
    private static final long REQUEST_TIMEOUT = 30000L;

    public NettyClientService(){
        this.serverConfig = new NettyServerConfig();
        this.clientConfig = new NettyClientConfig();
        this.isRunning = true;
    }

    public void startClient(){
        this.client = new NettyRemoteClient(clientConfig);
        this.isRunning = true;
        logger.info("netty client start");
    }

    /**
     *  发送命令，收到结果
     *
     * @return
     */
    public <T extends AbstractResponseCommand> AbstractResponseCommand sendCommand(ClusterType clusterType,
                                                                                   AbstractOperatorCommand abstractOperatorCommand,
                                                                                   Class<T> clazz){
        Command sendCommand = abstractOperatorCommand.convert2Command();
        final Host address = new Host(serverConfig.getServerHost(clusterType), serverConfig.getListenPort(clusterType));
        try {
            logger.info("Send command to Address:[{}],Command:[{}]",address.getAddress(), sendCommand);
            Command receivedCommand = this.client.sendSync(address, sendCommand, REQUEST_TIMEOUT);
            return JSONUtils.parseObject(receivedCommand.getBody(), clazz);
        } catch (Exception e) {
            logger.error("sync alert send error", e);
        } finally {
            logger.info("netty request over.");
        }

        return null;
    }

    /**
     *  发送命令，收到结果
     *
     * @return
     */
    public <T extends AbstractResponseCommand> AbstractResponseCommand sendCommandAsync(ClusterType clusterType,
                                                                                        AbstractOperatorCommand abstractOperatorCommand,
                                                                                        final InvokeCallback invokeCallback){
        Command sendCommand = abstractOperatorCommand.convert2Command();
        final Host address = new Host(serverConfig.getServerHost(clusterType), serverConfig.getListenPort(clusterType));
        try {
            logger.info("Send command to Address:[{}],Command:[{}]",address.getAddress(),sendCommand);
            this.client.sendAsync(address, sendCommand, REQUEST_TIMEOUT,invokeCallback);
        } catch (Exception e) {
            logger.error("sync alert send error", e);
        } finally {
            logger.info("netty request over.");
        }

        return null;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * close
     */
    public void close() {
        this.client.close();
        this.isRunning = false;
        logger.info("netty client closed");
    }
}
