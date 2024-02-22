package com.dpline.common.util;

import com.dpline.common.Constants;
import org.apache.commons.lang.StringUtils;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * parameter parse utils
 */
public class ParameterUtils {
    private static final String DATE_PARSE_PATTERN = "\\$\\[([^\\$\\]]+)]";

    private static final String DATE_START_PATTERN = "^[0-9]";

    private ParameterUtils() {
        throw new UnsupportedOperationException("Construct ParameterUtils");
    }

    /**
     * convert parameters place holders
     *
     * @param parameterString parameter
     * @param parameterMap    parameter map
     * @return convert parameters place holders
     */
    public static String convertParameterPlaceholders(String parameterString, Map<String, String> parameterMap) {
        if (StringUtils.isEmpty(parameterString)) {
            return parameterString;
        }
        Date cronTime;
        if (parameterMap != null && !parameterMap.isEmpty()) {
            // replace variable ${} form,refers to the replacement of system variables and custom variables
            parameterString = PlaceholderUtils.replacePlaceholders(parameterString, parameterMap, true);
        }
        if (parameterMap != null && null != parameterMap.get(Constants.PARAMETER_DATETIME)) {
            //Get current time, schedule execute time
            String cronTimeStr = parameterMap.get(Constants.PARAMETER_DATETIME);
            cronTime = DateUtils.parse(cronTimeStr, Constants.PARAMETER_FORMAT_TIME);
        } else {
            cronTime = new Date();
        }
        // replace time $[...] form, eg. $[yyyyMMdd]
        if (cronTime != null) {
            return dateTemplateParse(parameterString, cronTime);
        }
        return parameterString;
    }

    /**
     * handle escapes
     *
     * @param inputString input string
     * @return string filter escapes
     */
    public static String handleEscapes(String inputString) {

        if (!StringUtils.isEmpty(inputString)) {
            return inputString.replace("%", "////%").replaceAll("[\n|\r\t]", "_");
        }
        return inputString;
    }


    private static String dateTemplateParse(String templateStr, Date date) {
        if (templateStr == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(DATE_PARSE_PATTERN);

        StringBuffer newValue = new StringBuffer(templateStr.length());

        Matcher matcher = pattern.matcher(templateStr);

        while (matcher.find()) {
            String key = matcher.group(1);
            if (Pattern.matches(DATE_START_PATTERN, key)) {
                continue;
            }
            String value = TimePlaceholderUtils.getPlaceHolderTime(key, date);
            assert value != null;
            matcher.appendReplacement(newValue, value);
        }

        matcher.appendTail(newValue);

        return newValue.toString();
    }

}
