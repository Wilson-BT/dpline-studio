package com.dpline.remote;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.dpline.common.Constants;
import com.dpline.remote.code.NettyDecoder;
import com.dpline.remote.code.NettyEncoder;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.Host;
import com.dpline.remote.config.NettyClientConfig;
import com.dpline.remote.expection.RemotingException;
import com.dpline.remote.expection.RemotingTimeoutException;
import com.dpline.remote.future.InvokeCallback;
import com.dpline.remote.future.NamedThreadFactory;
import com.dpline.remote.future.ReleaseSemaphore;
import com.dpline.remote.future.ResponseFuture;
import com.dpline.remote.handle.NettyClientHandler;
import com.dpline.remote.handle.NettyRequestProcessor;
import com.dpline.remote.util.NettyUtils;
import com.dpline.remote.command.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * remoting netty client
 */
public class NettyRemoteClient {

    private final Logger logger = LoggerFactory.getLogger(NettyRemoteClient.class);

    /**
     * client bootstrap
     */
    private final Bootstrap bootstrap = new Bootstrap();

    /**
     * encoder
     */
    private final NettyEncoder encoder = new NettyEncoder();

    /**
     * channels
     */
    private final ConcurrentHashMap<Host, Channel> channels = new ConcurrentHashMap<>(128);

    /**
     * started flag
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * worker group
     */
    private final EventLoopGroup workerGroup;

    /**
     * client config
     */
    private final NettyClientConfig clientConfig;

    /**
     * saync semaphore
     */
    private final Semaphore asyncSemaphore = new Semaphore(200, true);

    /**
     * callback thread executor
     */
    private final ExecutorService callbackExecutor;

    /**
     * client handler
     */
    private final NettyClientHandler clientHandler;

    /**
     * response future executor
     */
    private final ScheduledExecutorService responseFutureExecutor;

    /**
     * client init
     *
     * @param clientConfig client config
     */
    public NettyRemoteClient(final NettyClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        if (NettyUtils.useEpoll()) {
            this.workerGroup = new EpollEventLoopGroup(clientConfig.getWorkerThreads(), new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyClient_%d", this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.workerGroup = new NioEventLoopGroup(clientConfig.getWorkerThreads(), new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyClient_%d", this.threadIndex.incrementAndGet()));
                }
            });
        }
        // 创建 容量为 5，最大容量为10的线程池
        this.callbackExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(1000), new NamedThreadFactory("CallbackExecutor", 10),
            new CallerThreadExecutePolicy());
        this.clientHandler = new NettyClientHandler(this, callbackExecutor);

        this.responseFutureExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ResponseFutureExecutor"));

        this.start();
    }

    /**
     * start
     */
    private void start() {

        this.bootstrap
            .group(this.workerGroup)
            .channel(NettyUtils.getSocketChannelClass())
            .option(ChannelOption.SO_KEEPALIVE, clientConfig.isSoKeepalive())
            .option(ChannelOption.TCP_NODELAY, clientConfig.isTcpNoDelay())
            .option(ChannelOption.SO_SNDBUF, clientConfig.getSendBufferSize())
            .option(ChannelOption.SO_RCVBUF, clientConfig.getReceiveBufferSize())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientConfig.getConnectTimeoutMillis())
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline()
                        // 心跳设置
                        .addLast("client-idle-handler", new IdleStateHandler(Constants.NETTY_CLIENT_HEART_BEAT_TIME, 0, 0, TimeUnit.MILLISECONDS))
                        .addLast(new NettyDecoder(), clientHandler, encoder);
                }
            });
        this.responseFutureExecutor.scheduleAtFixedRate(ResponseFuture::scanFutureTable, 5000, 1000, TimeUnit.MILLISECONDS);
        isStarted.compareAndSet(false, true);
    }

    /**
     * async send
     *
     * @param host host
     * @param command command
     * @param timeoutMillis timeoutMillis
     * @param invokeCallback callback function
     */
    public void sendAsync(final Host host, final Command command,
                          final long timeoutMillis,
                          final InvokeCallback invokeCallback) throws InterruptedException, RemotingException {
        final Channel channel = getChannel(host);
        if (channel == null) {
            throw new RemotingException("network error");
        }
        /*
         * request unique identification
         */
        final long opaque = command.getOpaque();
        /*
         *  control concurrency number
         */
        boolean acquired = this.asyncSemaphore.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (acquired) {
            final ReleaseSemaphore releaseSemaphore = new ReleaseSemaphore(this.asyncSemaphore);

            /*
             *  response future
             */
            final ResponseFuture responseFuture = new ResponseFuture(opaque,
                timeoutMillis,
                invokeCallback,
                releaseSemaphore);
            try {
                channel.writeAndFlush(command).addListener(future -> {
                    if (future.isSuccess()) {
                        responseFuture.setSendOk(true);
                        return;
                    } else {
                        responseFuture.setSendOk(false);
                    }
                    responseFuture.setCause(future.cause());
                    responseFuture.putResponse(null);
                    try {
                        responseFuture.executeInvokeCallback();
                    } catch (Exception ex) {
                        logger.error("execute callback error", ex);
                    } finally {
                        responseFuture.release();
                    }
                });
            } catch (Exception ex) {
                responseFuture.release();
                throw new RemotingException(String.format("send command to host: %s failed", host), ex);
            }
        } else {
            String message = String.format("try to acquire async semaphore timeout: %d, waiting thread num: %d, total permits: %d",
                timeoutMillis, asyncSemaphore.getQueueLength(), asyncSemaphore.availablePermits());
            throw new RemotingException(message);
        }
    }

    /**
     * sync send
     *
     * @param host host
     * @param command command
     * @param timeoutMillis timeoutMillis
     * @return command
     */
    public Command sendSync(final Host host, final Command command, final long timeoutMillis) throws InterruptedException, RemotingException {
        final Channel channel = getChannel(host);
        if (channel == null) {
            throw new RemotingException(String.format("connect to : %s fail", host));
        }
        final long opaque = command.getOpaque();
        final ResponseFuture responseFuture = new ResponseFuture(opaque, timeoutMillis, null, null);
        channel.writeAndFlush(command).addListener(future -> {
            if (future.isSuccess()) {
                responseFuture.setSendOk(true);
                return;
            } else {
                responseFuture.setSendOk(false);
            }
            responseFuture.setCause(future.cause());
            responseFuture.putResponse(null);
            logger.error("send command {} to host {} failed", command, host);
        });
        /*
         * sync wait for result
         */
        Command result = responseFuture.waitResponse();
        if (result == null) {
            if (responseFuture.isSendOK()) {
                throw new RemotingTimeoutException(host.toString(), timeoutMillis, responseFuture.getCause());
            } else {
                throw new RemotingException(host.toString(), responseFuture.getCause());
            }
        }
        return result;
    }

    /**
     * send task
     *
     * @param host host
     * @param command command
     */
    public void send(final Host host, final Command command) throws RemotingException {
        Channel channel = getChannel(host);
        if (channel == null) {
            throw new RemotingException(String.format("connect to : %s fail", host));
        }
        try {
            ChannelFuture future = channel.writeAndFlush(command).await();
            if (future.isSuccess()) {
                logger.debug("send command : {} , to : {} successfully.", command, host.getAddress());
            } else {
                String msg = String.format("send command : %s , to :%s failed", command, host.getAddress());
                logger.error(msg, future.cause());
                throw new RemotingException(msg);
            }
        } catch (RemotingException remotingException) {
            throw remotingException;
        } catch (Exception e) {
            logger.error("Send command {} to address {} encounter error.", command, host.getAddress());
            throw new RemotingException(String.format("Send command : %s , to :%s encounter error", command, host.getAddress()), e);
        }
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
        this.clientHandler.registerProcessor(commandType, processor, executor);
    }

    /**
     * get channel
     */
    public Channel getChannel(Host host) {
        Channel channel = channels.get(host);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        return createChannel(host, true);
    }

    /**
     * create channel
     *
     * @param host host
     * @param isSync sync flag
     * @return channel
     */
    public Channel createChannel(Host host, boolean isSync) {
        ChannelFuture future;
        try {
            synchronized (bootstrap) {
                future = bootstrap.connect(new InetSocketAddress(host.getIp(), host.getPort()));
            }
            if (isSync) {
                future.sync();
            }
            if (future.isSuccess()) {
                Channel channel = future.channel();
                channels.put(host, channel);
                return channel;
            }
        } catch (Exception ex) {
            logger.warn(String.format("connect to %s error", host), ex);
        }
        return null;
    }

    /**
     * close
     */
    public void close() {
        if (isStarted.compareAndSet(true, false)) {
            try {
                closeChannels();
                if (workerGroup != null) {
                    this.workerGroup.shutdownGracefully();
                }
                if (callbackExecutor != null) {
                    this.callbackExecutor.shutdownNow();
                }
                if (this.responseFutureExecutor != null) {
                    this.responseFutureExecutor.shutdownNow();
                }
                logger.info("netty client closed");
            } catch (Exception ex) {
                logger.error("netty client close exception", ex);
            }
        }
    }

    /**
     * close channels
     */
    private void closeChannels() {
        for (Channel channel : this.channels.values()) {
            channel.close();
        }
        this.channels.clear();
    }

    /**
     * close channel
     *
     * @param host host
     */
    public void closeChannel(Host host) {
        Channel channel = this.channels.remove(host);
        if (channel != null) {
            channel.close();
        }
    }
}
