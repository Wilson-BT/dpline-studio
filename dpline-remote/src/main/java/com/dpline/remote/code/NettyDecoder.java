
package com.dpline.remote.code;

import com.dpline.remote.command.CommandHeader;
import com.dpline.remote.command.Command;
import com.dpline.remote.command.CommandContext;
import com.dpline.remote.command.CommandType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * netty decoder
 */
public class NettyDecoder extends ReplayingDecoder<NettyDecoder.State> {
    private static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);

    public NettyDecoder() {
        super(State.MAGIC);
    }

    private final CommandHeader commandHeader = new CommandHeader();

    /**
     * decode
     *
     * @param ctx channel handler context
     * @param in byte buffer
     * @param out out content
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case MAGIC:
                checkMagic(in.readByte());
                checkpoint(State.VERSION);
                // fallthru
            case VERSION:
                checkVersion(in.readByte());
                checkpoint(State.COMMAND);
                // fallthru
            case COMMAND:
                commandHeader.setType(in.readByte());
                checkpoint(State.OPAQUE);
                // fallthru
            case OPAQUE:
                commandHeader.setOpaque(in.readLong());
                checkpoint(State.CONTEXT_LENGTH);
                // fallthru
            case CONTEXT_LENGTH:
                commandHeader.setContextLength(in.readInt());
                checkpoint(State.CONTEXT);
                // fallthru
            case CONTEXT:
                byte[] context = new byte[commandHeader.getContextLength()];
                in.readBytes(context);
                commandHeader.setContext(context);
                checkpoint(State.BODY_LENGTH);
                // fallthru
            case BODY_LENGTH:
                commandHeader.setBodyLength(in.readInt());
                checkpoint(State.BODY);
                // fallthru
            case BODY:
                byte[] body = new byte[commandHeader.getBodyLength()];
                in.readBytes(body);
                Command packet = new Command();
                packet.setType(commandType(commandHeader.getType()));
                packet.setOpaque(commandHeader.getOpaque());
                packet.setContext(CommandContext.valueOf(commandHeader.getContext()));
                packet.setBody(body);
                out.add(packet);
                //
                checkpoint(State.MAGIC);
                break;
            default:
                logger.warn("unknown decoder state {}", state());
        }
    }

    /**
     * get command type
     *
     * @param type type
     */
    private CommandType commandType(byte type) {
        for (CommandType ct : CommandType.values()) {
            if (ct.ordinal() == type) {
                return ct;
            }
        }
        return null;
    }

    /**
     * check magic
     *
     * @param magic magic
     */
    private void checkMagic(byte magic) {
        if (magic != Command.MAGIC) {
            throw new IllegalArgumentException("illegal packet [magic]" + magic);
        }
    }

    /**
     * check version
     */
    private void checkVersion(byte version) {
        if (version != Command.VERSION) {
            throw new IllegalArgumentException("illegal protocol [version]" + version);
        }
    }

    enum State {
        MAGIC,
        VERSION,
        COMMAND,
        OPAQUE,
        CONTEXT_LENGTH,
        CONTEXT,
        BODY_LENGTH,
        BODY;
    }
}
