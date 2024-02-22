
package com.dpline.remote.command;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  receive task log request command and content fill
 *  for netty data serializable transfer
 */
public class Command implements Serializable {

    private static final AtomicLong REQUEST_ID = new AtomicLong(1);

    public static final byte MAGIC = (byte) 0xbabe;
    public static final byte VERSION = 0;

    public Command(){
        this.opaque = REQUEST_ID.getAndIncrement();
    }

    public Command(long opaque){
        this.opaque = opaque;
    }

    /**
     * command type
     */
    private CommandType type;

    /**
     *  request unique identification
     */
    private long opaque;

    /**
     * request context
     */
    private CommandContext context = new CommandContext();

    /**
     *  data body
     */
    private byte[] body;

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public CommandContext getContext() {
        return context;
    }

    public void setContext(CommandContext context) {
        this.context = context;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (opaque ^ (opaque >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Command other = (Command) obj;
        return opaque == other.opaque;
    }

    @Override
    public String toString() {
        return "Command [type=" + type + ", opaque=" + opaque + ", bodyLen=" + (body == null ? 0 : body.length) + "]";
    }

}
