package com.dpline.remote.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;

/**
 *  ping machine
 */
public class Ping implements Serializable {

    /**
     *  ping body
     */
    protected static final ByteBuf EMPTY_BODY = Unpooled.EMPTY_BUFFER;

    /**
     *  request command body
     */
    private static final byte[] EMPTY_BODY_ARRAY = new byte[0];

    private static final ByteBuf PING_BUF;

    static {
        ByteBuf ping = Unpooled.buffer();
        ping.writeByte(Command.MAGIC);
        ping.writeByte(CommandType.PING.ordinal());
        ping.writeLong(0);
        ping.writeInt(0);
        ping.writeBytes(EMPTY_BODY);
        PING_BUF = Unpooled.unreleasableBuffer(ping).asReadOnly();
    }

    /**
     *  ping content
     * @return result
     */
    public static ByteBuf pingContent(){
        return PING_BUF.duplicate();
    }

    /**
     *  create ping command
     *
     * @return command
     */
    public static Command create(){
        Command command = new Command();
        command.setType(CommandType.PING);
        command.setBody(EMPTY_BODY_ARRAY);
        return command;
    }
}
