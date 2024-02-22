package com.handsome.remote.netty;

import com.handsome.remote.NettyRemoteClient;
import com.handsome.remote.NettyRemoteServer;
import com.handsome.remote.command.*;
import com.handsome.remote.config.NettyClientConfig;
import com.handsome.remote.config.NettyServerConfig;
import com.handsome.remote.future.InvokeCallback;
import com.handsome.remote.future.ResponseFuture;
import com.handsome.remote.handle.NettyRequestProcessor;
import io.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class NettyRemoteClientTest {

    public static final Logger logger = LoggerFactory.getLogger(NettyRemoteClientTest.class);

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

        server.start();
        //
        final NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemoteClient client = new NettyRemoteClient(clientConfig);
        Command commandPing = Ping.create();
        try {
            Command response = client.sendSync(new Host("127.0.0.1", serverConfig.getListenPort()), commandPing, 2000);
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

        server.start();
        final NettyClientConfig clientConfig = new NettyClientConfig();
        NettyRemoteClient client = new NettyRemoteClient(clientConfig);
        CountDownLatch latch = new CountDownLatch(1);
        Command commandPing = Ping.create();
        try {
            final AtomicLong opaque = new AtomicLong(0);
            client.sendAsync(new Host("127.0.0.1", serverConfig.getListenPort()), commandPing, 2000, new InvokeCallback() {
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
