package com.handsome.remote;

import com.handsome.common.Constants;
import com.handsome.remote.config.NettyServerConfig;
import com.handsome.remote.util.NettyUtils;
import com.handsome.remote.code.NettyDecoder;
import com.handsome.remote.code.NettyEncoder;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.expection.RemoteException;
import com.handsome.remote.handle.NettyRequestProcessor;
import com.handsome.remote.handle.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyRemoteServer {

    /**
     * remoting netty server
     */
    private final Logger logger = LoggerFactory.getLogger(NettyRemoteServer.class);


    /**
     * server bootstrap
     */
    private final ServerBootstrap serverBootstrap = new ServerBootstrap();


    private final NettyServerConfig serverConfig;

    /**
     * boss group
     */
    private final EventLoopGroup bossGroup;

    /**
     * worker group
     */
    private final EventLoopGroup workGroup;

    /**
     * started flag
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * encoder
     */
    private final NettyEncoder encoder = new NettyEncoder();


    /**
     * server handler
     */
    private final NettyServerHandler serverHandler = new NettyServerHandler(this);



    /**
     * default executor
     */
    private final ExecutorService defaultExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Netty server bind fail message
     */
    private static final String NETTY_BIND_FAILURE_MSG = "NettyRemotingServer bind %s fail";

    /**
     * server init
     * @param serverConfig client config
     */
    public NettyRemoteServer(final NettyServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        if (NettyUtils.useEpoll()) {
            this.bossGroup = new EpollEventLoopGroup(1, new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerBossThread_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.workGroup = new EpollEventLoopGroup(serverConfig.getWorkerThread(), new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerWorkerThread_%d", this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.bossGroup = new NioEventLoopGroup(1, new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerBossThread_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.workGroup = new NioEventLoopGroup(serverConfig.getWorkerThread(), new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerWorkerThread_%d", this.threadIndex.incrementAndGet()));
                }
            });
        }
    }

    /**
     * server start
     */
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            this.serverBootstrap
                .group(this.bossGroup, this.workGroup)
                .channel(NettyUtils.getServerSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, serverConfig.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, serverConfig.isSoKeepalive())
                .childOption(ChannelOption.TCP_NODELAY, serverConfig.isTcpNoDelay())
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBufferSize())
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBufferSize())
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        initNettyChannel(ch);
                    }
                });

            ChannelFuture future;
            try {
                future = serverBootstrap.bind(serverConfig.getListenPort()).sync();
            } catch (Exception e) {
                logger.error("NettyRemotingServer bind fail {}, exit", e.getMessage(), e);
                throw new RemoteException(String.format(NETTY_BIND_FAILURE_MSG, serverConfig.getListenPort()));
            }
            if (future.isSuccess()) {
                logger.info("NettyRemotingServer bind success at port : {}", serverConfig.getListenPort());
            } else if (future.cause() != null) {
                throw new RemoteException(String.format(NETTY_BIND_FAILURE_MSG, serverConfig.getListenPort()), future.cause());
            } else {
                throw new RemoteException(String.format(NETTY_BIND_FAILURE_MSG, serverConfig.getListenPort()));
            }
        }
    }

    /**
     * init netty channel
     *
     * @param ch socket channel
     */
    private void initNettyChannel(SocketChannel ch) {
        ch.pipeline()
            .addLast("encoder", encoder)
            .addLast("decoder", new NettyDecoder())
            .addLast("server-idle-handle", new IdleStateHandler(0, 0, Constants.NETTY_SERVER_HEART_BEAT_TIME, TimeUnit.MILLISECONDS))
            .addLast("handler", serverHandler);
    }

    /**
     * get default thread executor，用于执行任务
     *
     * @return thread executor
     */
    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * register processor
     *
     * @param commandType command type
     * @param processor processor
     */
    public void registerProcessor(final CommandType commandType, final NettyRequestProcessor processor) {
        this.registerProcessor(commandType, processor, null);
    }

    /**
     * register processor
     *
     * @param commandType command type
     * @param processor processor
     * @param executor thread executor
     */
    public void registerProcessor(final CommandType commandType, final NettyRequestProcessor processor, final ExecutorService executor) {
        this.serverHandler.registerProcessor(commandType, processor, executor);
    }

    public void close() {
        if (isStarted.compareAndSet(true, false)) {
            try {
                if (bossGroup != null) {
                    this.bossGroup.shutdownGracefully();
                }
                if (workGroup != null) {
                    this.workGroup.shutdownGracefully();
                }
                defaultExecutor.shutdown();
            } catch (Exception ex) {
                logger.error("netty server close exception", ex);
            }
            logger.info("netty server closed");
        }
    }


}
