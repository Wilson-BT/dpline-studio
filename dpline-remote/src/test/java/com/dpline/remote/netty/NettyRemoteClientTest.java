package com.dpline.remote.netty;

import com.dpline.common.enums.ClusterType;
import com.dpline.remote.command.*;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.remote.NettyRemoteClient;
import com.dpline.remote.NettyRemoteServer;
import com.dpline.remote.config.NettyClientConfig;
import com.dpline.remote.config.NettyServerConfig;
import com.dpline.remote.future.InvokeCallback;
import com.dpline.remote.future.ResponseFuture;
import io.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class NettyRemoteClientTest {

    private static final Logger logger = LoggerFactory.getLogger(NettyRemoteClientTest.class);

    /**
     * test send sync
     */
    @Test
    public void testSendSync() {
        NettyServerConfig serverConfig = new NettyServerConfig();

        NettyRemoteServer server = new NettyRemoteServer(serverConfig);
        server.registerProcessor(CommandType.PING, new NettyRequestProcessor() {
            @Override
            public void process(Channel channel, Command command) {
                logger.info(command.toString());
                channel.writeAndFlush(Pong.create(command.getOpaque()));
            }
        });

        server.start(ClusterType.KUBERNETES);
        //
        final NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemoteClient client = new NettyRemoteClient(clientConfig);
        Command commandPing = Ping.create();
        try {
            Command response = client.sendSync(new Host("127.0.0.1", serverConfig.getListenPort(ClusterType.KUBERNETES)), commandPing, 2000);
            Assert.assertEquals(commandPing.getOpaque(), response.getOpaque());
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.close();
        client.close();
    }

    /**
     * test sned async
     */
    @Test
    public void testSendAsync() {
        NettyServerConfig serverConfig = new NettyServerConfig();

        NettyRemoteServer server = new NettyRemoteServer(serverConfig);
        server.registerProcessor(CommandType.PING, new NettyRequestProcessor() {
            @Override
            public void process(Channel channel, Command command) {
                logger.info(command.toString());
                channel.writeAndFlush(Pong.create(command.getOpaque()));
            }
        });

        server.start(ClusterType.KUBERNETES);
        final NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemoteClient client = new NettyRemoteClient(clientConfig);
        CountDownLatch latch = new CountDownLatch(1);
        Command commandPing = Ping.create();
        try {
            final AtomicLong opaque = new AtomicLong(0);
            client.sendAsync(new Host("127.0.0.1", serverConfig.getListenPort(ClusterType.KUBERNETES)), commandPing, 2000, new InvokeCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    logger.info(responseFuture.getResponseCommand().toString());
                    opaque.set(responseFuture.getOpaque());
                    latch.countDown();
                }
            });
            latch.await();
            Assert.assertEquals(commandPing.getOpaque(), opaque.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.close();
        client.close();
    }



    @Test
    public void testStopSendAsync() {
        NettyServerConfig serverConfig = new NettyServerConfig();

        NettyRemoteServer server = new NettyRemoteServer(serverConfig);
        server.registerProcessor(CommandType.PING, new NettyRequestProcessor() {
            @Override
            public void process(Channel channel, Command command) {
                logger.info(command.toString());
                channel.writeAndFlush(Pong.create(command.getOpaque()));
            }
        });

        server.start(ClusterType.KUBERNETES);
        final NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemoteClient client = new NettyRemoteClient(clientConfig);
        CountDownLatch latch = new CountDownLatch(1);
        Command commandPing = Ping.create();
        try {
            final AtomicLong opaque = new AtomicLong(0);
            client.sendAsync(new Host("127.0.0.1", serverConfig.getListenPort(ClusterType.KUBERNETES)), commandPing, 2000, new InvokeCallback() {
                @Override
                public void operationComplete(ResponseFuture responseFuture) {
                    logger.info(responseFuture.getResponseCommand().toString());
                    opaque.set(responseFuture.getOpaque());
                    latch.countDown();
                }
            });
            latch.await();
            Assert.assertEquals(commandPing.getOpaque(), opaque.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.close();
        client.close();
    }


}
