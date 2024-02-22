

package com.handsome.common.util;

import com.handsome.common.Constants;

import java.util.Collection;
import java.util.Iterator;

/**
 * java.lang.String utils class
 */
public class StringUtils {

    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * The empty String {@code ""}.
     */
    public static final String EMPTY = "";

    private StringUtils() {
        throw new UnsupportedOperationException("Construct StringUtils");
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * <p>Checks if a CharSequence is empty (""), null or whitespace only.</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null and not whitespace only
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling {@code null} by returning
     * {@code null}.</p>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * whitespace, empty ("") or {@code null}, the value of {@code defaultStr}.</p>
     *
     * @param <T> the specific kind of CharSequence
     * @param str the CharSequence to check, may be null
     * @param defaultStr  the default CharSequence to return
     *  if the input is whitespace, empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * <p>Compares two String, returning {@code true} if they represent
     * equal string, ignoring case.</p>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return {@code true} if the String are equal, case insensitive, or
     *  both {@code null}
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    /**
     * <p>Joins the elements of the provided Collection into a single String
     * containing the provided Collection of elements.</p>
     *
     * @param collection the collection, may be null
     * @param separator the separator
     * @return a single String
     */
    public static String join(Collection<?> collection, String separator) {
        return collection == null ? null : join(collection.iterator(), separator);
    }

    /**
     * <p>Joins the elements of the provided Iterator into a single String
     * containing the provided Iterator of elements.</p>
     *
     * @param iterator the iterator, may be null
     * @param separator the separator
     * @return a single String
     */
    public static String join(Iterator<?> iterator, String separator) {
        if (iterator == null) {
            return null;
        } else if (!iterator.hasNext()) {
            return "";
        } else {
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return first == null ? "" : first.toString();
            } else {
                StringBuilder buf = new StringBuilder(256);
                if (first != null) {
                    buf.append(first);
                }

                while (iterator.hasNext()) {
                    if (separator != null) {
                        buf.append(separator);
                    }

                    Object obj = iterator.next();
                    if (obj != null) {
                        buf.append(obj);
                    }
                }
                return buf.toString();
            }
        }
    }
    public static String [] strSplitToArray(String str) {
        return str.replace(Constants.LEFT_BRACKETS, Constants.BLACK)
                .replace(Constants.RIGHT_BRACKETS, Constants.BLACK)
                .split(Constants.COMMA);
    }

    public static String byteToHexString(byte[] bytes) {
        return byteToHexString(bytes, 0, bytes.length);
    }

    public static String byteToHexString(byte[] bytes, int start, int end) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null");
        } else {
            int length = end - start;
            char[] out = new char[length * 2];
            int i = start;

            for(int var6 = 0; i < end; ++i) {
                out[var6++] = HEX_CHARS[(240 & bytes[i]) >>> 4];
                out[var6++] = HEX_CHARS[15 & bytes[i]];
            }

            return new String(out);
        }
    }
    public static byte[] hexStringToByte(String hex) {
        byte[] bts = new byte[hex.length() / 2];

        for(int i = 0; i < bts.length; ++i) {
            bts[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return bts;
    }

}
