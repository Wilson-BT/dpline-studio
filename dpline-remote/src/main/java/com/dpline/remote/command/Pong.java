package com.dpline.remote.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;

/**
 * Pong return after ping
 */
public class Pong implements Serializable {

    /**
     *  pong body
     */
    protected static final ByteBuf EMPTY_BODY = Unpooled.EMPTY_BUFFER;

    /**
     *  pong command body
     */
    private static final byte[] EMPTY_BODY_ARRAY = new byte[0];

    /**
     *  ping byte buffer
     */
    private static final ByteBuf PONG_BUF;

    static {
        ByteBuf ping = Unpooled.buffer();
        ping.writeByte(Command.MAGIC);
        ping.writeByte(CommandType.PONG.ordinal());
        ping.writeLong(0);
        ping.writeInt(0);
        ping.writeBytes(EMPTY_BODY);
        PONG_BUF = Unpooled.unreleasableBuffer(ping).asReadOnly();
    }

    /**
     *  ping content
     * @return result
     */
    public static ByteBuf pingContent(){
        return PONG_BUF.duplicate();
    }

    /**
     * package pong command
     *
     * @param opaque request unique identification
     * @return command
     */
    public static Command create(long opaque){
        Command command = new Command(opaque);
        command.setType(CommandType.PONG);
        command.setBody(EMPTY_BODY_ARRAY);
        return command;
    }
}
