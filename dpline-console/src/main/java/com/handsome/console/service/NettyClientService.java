package com.handsome.console.service;

import com.handsome.common.util.JSONUtils;
import com.handsome.remote.NettyRemoteClient;
import com.handsome.remote.command.AbstractOperatorCommand;
import com.handsome.remote.command.Command;
import com.handsome.remote.command.Host;
import com.handsome.remote.config.NettyClientConfig;
import com.handsome.remote.config.NettyServerConfig;
import com.handsome.remote.expection.RemotingException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * netty client
 */
@Data
@Component
public class NettyClientService {

    private Logger logger = LoggerFactory.getLogger(NettyClientService.class);

    private String host;

    private int port;

    private NettyClientConfig clientConfig;

    private NettyRemoteClient client;

    private boolean isRunning;

    /**
     * request time out
     */
    private static final long REQUEST_TIMEOUT = 30000 * 1000L;

    public NettyClientService(){
        NettyServerConfig nettyServerConfig = new NettyServerConfig();
        this.clientConfig = new NettyClientConfig();
        this.isRunning = true;
        this.host = nettyServerConfig.getServerHost();
        this.port = nettyServerConfig.getListenPort();
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
    public <T extends AbstractOperatorCommand> AbstractOperatorCommand sendCommand(AbstractOperatorCommand abstractOperatorCommand,Class<T> clazz){
        Command sendCommand = abstractOperatorCommand.convert2Command();
        final Host address = new Host(host, port);
        try {
            Command receivedCommand = this.client.sendSync(address, sendCommand, REQUEST_TIMEOUT);
            return JSONUtils.deserialize(receivedCommand.getBody(), clazz);
        } catch (InterruptedException | RemotingException e) {
            e.printStackTrace();
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
