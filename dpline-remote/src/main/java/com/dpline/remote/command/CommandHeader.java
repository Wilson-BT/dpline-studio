
package com.dpline.remote.command;

import java.io.Serializable;

/**
 *  command header
 */
public class CommandHeader implements Serializable {

    /**
     * type
     */
    private byte type;

    /**
     * request unique identification
     */
    private long opaque;

    /**
     *  context length
     */
    private int contextLength;

    /**
     *  context
     */
    private byte[] context;

    /**
     *  body length
     */
    private int bodyLength;

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public int getContextLength() {
        return contextLength;
    }

    public void setContextLength(int contextLength) {
        this.contextLength = contextLength;
    }

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }
}
