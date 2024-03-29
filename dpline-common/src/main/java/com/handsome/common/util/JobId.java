package com.handsome.common.util;


import java.io.Serializable;
import java.util.Random;

public class JobId implements Comparable<JobId>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Random RND = new Random();
    private static final int SIZE_OF_LONG = 8;
    public static final int SIZE = 16;
    protected final long upperPart;
    protected final long lowerPart;
    private transient String hexString;

    public JobId(byte[] bytes) {
        if (bytes != null && bytes.length == 16) {
            this.lowerPart = byteArrayToLong(bytes, 0);
            this.upperPart = byteArrayToLong(bytes, 8);
        } else {
            throw new IllegalArgumentException("Argument bytes must by an array of 16 bytes");
        }
    }

    public JobId(long lowerPart, long upperPart) {
        this.lowerPart = lowerPart;
        this.upperPart = upperPart;
    }

    public JobId(JobId id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        } else {
            this.lowerPart = id.lowerPart;
            this.upperPart = id.upperPart;
        }
    }

    public JobId() {
        this.lowerPart = RND.nextLong();
        this.upperPart = RND.nextLong();
    }

    public long getLowerPart() {
        return this.lowerPart;
    }

    public long getUpperPart() {
        return this.upperPart;
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[16];
        longToByteArray(this.lowerPart, bytes, 0);
        longToByteArray(this.upperPart, bytes, 8);
        return bytes;
    }

    public final String toHexString() {
        if (this.hexString == null) {
            byte[] ba = new byte[16];
            longToByteArray(this.lowerPart, ba, 0);
            longToByteArray(this.upperPart, ba, 8);
            this.hexString = StringUtils.byteToHexString(ba);
        }

        return this.hexString;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            JobId that = (JobId)obj;
            return that.lowerPart == this.lowerPart && that.upperPart == this.upperPart;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return (int)this.lowerPart ^ (int)(this.lowerPart >>> 32) ^ (int)this.upperPart ^ (int)(this.upperPart >>> 32);
    }

    public String toString() {
        return this.toHexString();
    }

    public int compareTo(JobId o) {
        int diff1 = Long.compare(this.upperPart, o.upperPart);
        int diff2 = Long.compare(this.lowerPart, o.lowerPart);
        return diff1 == 0 ? diff2 : diff1;
    }

    private static long byteArrayToLong(byte[] ba, int offset) {
        long l = 0L;

        for(int i = 0; i < 8; ++i) {
            l |= ((long)ba[offset + 8 - 1 - i] & 255L) << (i << 3);
        }

        return l;
    }

    private static void longToByteArray(long l, byte[] ba, int offset) {
        for(int i = 0; i < 8; ++i) {
            int shift = i << 3;
            ba[offset + 8 - 1 - i] = (byte)((int)((l & 255L << shift) >>> shift));
        }
    }

    public static JobId fromHexString(String hexString) {
        try {
            return new JobId(StringUtils.hexStringToByte(hexString));
        } catch (Exception var2) {
            throw new IllegalArgumentException("Cannot parse JobID from \"" + hexString + "\". The expected format is [0-9a-fA-F]{32}, e.g. fd72014d4c864993a2e5a9287b4a9c5d.", var2);
        }
    }
}
