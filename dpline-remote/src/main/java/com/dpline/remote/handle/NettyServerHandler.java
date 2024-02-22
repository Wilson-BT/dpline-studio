
package com.dpline.remote.handle;

import com.dpline.common.util.StringUtils;
import com.dpline.remote.command.Command;
import com.dpline.remote.util.ChannelUtils;
import com.dpline.remote.util.Pair;
import com.dpline.remote.NettyRemoteServer;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import com.dpline.remote.command.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;


/**
 * netty server request handler
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * netty remote server
     */
    private final NettyRemoteServer nettyRemotingServer;

    /**
     * server processors queue
     */
    private final ConcurrentHashMap<CommandType, Pair<NettyRequestProcessor, ExecutorService>> processors = new ConcurrentHashMap<>();

    public NettyServerHandler(NettyRemoteServer nettyRemotingServer) {
        this.nettyRemotingServer = nettyRemotingServer;
    }

    /**
     * When the current channel is not active,
     * the current channel has reached the end of its life cycle
     *
     * @param ctx channel handler context
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.channel().close();
    }

    /**
     * The current channel reads data from the remote end
     *
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        processReceived(ctx.channel(), (Command) msg);
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
        ExecutorService executorRef = executor;
        if (executorRef == null) {
            executorRef = nettyRemotingServer.getDefaultExecutor();
        }
        this.processors.putIfAbsent(commandType, new Pair<>(processor, executorRef));
    }

    /**
     * process received logic
     *
     * @param channel channel
     * @param msg message
     */
    private void processReceived(final Channel channel, final Command msg) {
        final CommandType commandType = msg.getType();
        if (CommandType.HEART_BEAT.equals(commandType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("server receive heart beat from: host: {}", ChannelUtils.getRemoteAddress(channel));
            }
            return;
        }
        final Pair<NettyRequestProcessor, ExecutorService> pair = processors.get(commandType);
        if (pair != null) {
            Runnable r = () -> {
                try {

                    pair.getLeft().process(channel, msg);
                } catch (Exception ex) {
                    logger.error("process msg {} error", msg, ex);
                }
            };
            try {
                pair.getRight().submit(r);
            } catch (RejectedExecutionException e) {
                logger.warn("thread pool is full, discard msg {} from {}", msg, ChannelUtils.getRemoteAddress(channel));
            }
        } else {
            logger.warn("commandType {} not support", commandType);
        }
    }

    /**
     * caught exception
     *
     * @param ctx channel handler context
     * @param cause cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught : {}", cause.getMessage(), cause);
        ctx.channel().close();
    }

    /**
     * channel write changed
     *
     * @param ctx channel handler context
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        ChannelConfig config = ch.config();

        if (!ch.isWritable()) {
            if (logger.isWarnEnabled()) {
                logger.warn("{} is not writable, over high water level : {}",
                        ch, config.getWriteBufferHighWaterMark());
            }

            config.setAutoRead(false);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("{} is writable, to low water : {}",
                        ch, config.getWriteBufferLowWaterMark());
            }
            config.setAutoRead(true);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
