

package com.dpline.remote.code;

import com.dpline.remote.command.Command;
import com.dpline.remote.expection.RemoteException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * netty encoder
 * @Sharable 可以在多个channel 中共享使用
 */
@Sharable
public class NettyEncoder extends MessageToByteEncoder<Command> {

    /**
     * encode
     *
     * @param ctx channel handler context
     * @param msg command
     * @param out byte buffer
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        if (msg == null) {
            throw new RemoteException("encode msg is null");
        }
        out.writeByte(Command.MAGIC);
        out.writeByte(Command.VERSION);
        out.writeByte(msg.getType().ordinal());
        out.writeLong(msg.getOpaque());
        writeContext(msg, out);
        out.writeInt(msg.getBody().length);
        out.writeBytes(msg.getBody());
    }

    private void writeContext(Command msg, ByteBuf out) {
        byte[] headerBytes = msg.getContext().toBytes();
        out.writeInt(headerBytes.length);
        out.writeBytes(headerBytes);
    }
}

