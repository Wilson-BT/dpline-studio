package com.dpline.common.util;

import java.util.regex.Pattern;

/**
 * This is Regex expression utils.
 */
public class RegexUtils {

    private static final String LINUX_USERNAME_PATTERN = "[a-z_][a-z\\d_]{0,30}";

    private RegexUtils() {
    }

    /**
     * check if the input is a valid linux username
     * @param str input
     * @return boolean
     */
    public static boolean isValidLinuxUserName(String str) {
        Pattern pattern = Pattern.compile(LINUX_USERNAME_PATTERN);
        return pattern.matcher(str).matches();
    }

    public static String escapeNRT(String str) {
        // Logging should not be vulnerable to injection attacks: Replace pattern-breaking characters
        if (str != null && !str.isEmpty()) {
            return str.replaceAll("[\n|\r|\t]", "_");
        }
        return null;
    }

}
