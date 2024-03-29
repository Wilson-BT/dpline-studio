package com.handsome.remote.handle;

import com.handsome.common.Constants;
import com.handsome.remote.NettyRemoteClient;
import com.handsome.remote.command.Command;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.future.ResponseFuture;
import com.handsome.remote.util.ChannelUtils;
import com.handsome.remote.util.Pair;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * netty client request handler
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    /**
     * netty client
     */
    private final NettyRemoteClient nettyRemoteClient;

    /**
     * callback thread executor
     */
    private final ExecutorService callbackExecutor;

    private static byte[] heartBeatData = "heart_beat".getBytes();

    /**
     * processors
     */
    private final ConcurrentHashMap<CommandType, Pair<NettyRequestProcessor, ExecutorService>> processors;

    /**
     * default executor
     */
    private final ExecutorService defaultExecutor = Executors.newFixedThreadPool(Constants.CPUS);

    public NettyClientHandler(NettyRemoteClient nettyRemoteClient, ExecutorService callbackExecutor) {
        this.nettyRemoteClient = nettyRemoteClient;
        this.callbackExecutor = callbackExecutor;
        this.processors = new ConcurrentHashMap<>();
    }

    /**
     * When the current channel is not active,
     * the current channel has reached the end of its life cycle
     *
     * @param ctx channel handler context
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        nettyRemoteClient.closeChannel(ChannelUtils.toAddress(ctx.channel()));
        ctx.channel().close();
    }

    /**
     * The current channel reads data from the remote
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
            executorRef = defaultExecutor;
        }
        this.processors.putIfAbsent(commandType, new Pair<>(processor, executorRef));
    }

    /**
     * process received logic
     *
     * @param command command
     */
    private void processReceived(final Channel channel, final Command command) {
        ResponseFuture future = ResponseFuture.getFuture(command.getOpaque());
        if (future != null) {
            future.setResponseCommand(command);
            future.release();
            if (future.getInvokeCallback() != null) {
                this.callbackExecutor.submit(future::executeInvokeCallback);
            } else {
                future.putResponse(command);
            }
        } else {
            processByCommandType(channel, command);
        }
    }

    public void processByCommandType(final Channel channel, final Command command) {
        final Pair<NettyRequestProcessor, ExecutorService> pair = processors.get(command.getType());
        if (pair != null) {
            Runnable run = () -> {
                try {
                    pair.getLeft().process(channel, command);
                } catch (Exception e) {
                    logger.error(String.format("process command %s exception", command), e);
                }
            };
            try {
                pair.getRight().submit(run);
            } catch (RejectedExecutionException e) {
                logger.warn("thread pool is full, discard command {} from {}", command, ChannelUtils.getRemoteAddress(channel));
            }
        } else {
            logger.warn("receive response {}, but not matched any request ", command);
        }
    }

    /**
     * caught exception
     *
     * @param ctx channel handler context
     * @param cause cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught : {}", cause.getMessage(), cause);
        nettyRemoteClient.closeChannel(ChannelUtils.toAddress(ctx.channel()));
        ctx.channel().close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Command heartBeat = new Command();
            heartBeat.setType(CommandType.HEART_BEAT);
            heartBeat.setBody(heartBeatData);
            ctx.channel().writeAndFlush(heartBeat)
                .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            if (logger.isDebugEnabled()) {
                logger.debug("Client send heart beat to: {}", ChannelUtils.getRemoteAddress(ctx.channel()));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
