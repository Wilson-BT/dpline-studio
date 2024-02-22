package com.dpline.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * encryption utils
 */
public class EncryptionUtils {

    private EncryptionUtils() {
        throw new UnsupportedOperationException("Construct EncryptionUtils");
    }

    /**
     * @param rawStr raw string
     * @return md5(rawStr)
     */
    public static String getMd5(String rawStr) {
        return DigestUtils.md5Hex(null == rawStr ? StringUtils.EMPTY : rawStr);
    }

}
