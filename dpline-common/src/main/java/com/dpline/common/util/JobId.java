package com.dpline.common.util;


import java.nio.ByteBuffer;

public class JobId extends AbstractID {
    private static final long serialVersionUID = 1L;

    public JobId() {
    }

    public JobId(long lowerPart, long upperPart) {
        super(lowerPart, upperPart);
    }

    public JobId(byte[] bytes) {
        super(bytes);
    }

    public static JobId generate() {
        return new JobId();
    }

    public static JobId fromByteArray(byte[] bytes) {
        return new JobId(bytes);
    }

    public static JobId fromByteBuffer(ByteBuffer buf) {
        long lower = buf.getLong();
        long upper = buf.getLong();
        return new JobId(lower, upper);
    }

    public static JobId fromHexString(String hexString) {
        try {
            return new JobId(StringUtils.hexStringToByte(hexString));
        } catch (Exception var2) {
            throw new IllegalArgumentException("Cannot parse JobID from \"" + hexString + "\". The expected format is [0-9a-fA-F]{32}, e.g. fd72014d4c864993a2e5a9287b4a9c5d.", var2);
        }
    }
}
