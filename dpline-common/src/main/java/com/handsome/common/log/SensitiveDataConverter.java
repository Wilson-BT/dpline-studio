
package com.handsome.common.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.handsome.common.Constants;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * sensitive data log converter
 */
public class SensitiveDataConverter extends MessageConverter {

    /**
     * password pattern
     */
    private final Pattern pwdPattern = Pattern.compile(Constants.DATASOURCE_PASSWORD_REGEX);


    @Override
    public String convert(ILoggingEvent event) {

        // get original log
        String requestLogMsg = event.getFormattedMessage();

        // desensitization log
        return convertMsg(requestLogMsg);
    }

    /**
     * deal with sensitive log
     *
     * @param oriLogMsg original log
     */
    private String convertMsg(final String oriLogMsg) {

        String tempLogMsg = oriLogMsg;

        if (!StringUtils.isEmpty(tempLogMsg)) {
            tempLogMsg = passwordHandler(pwdPattern, tempLogMsg);
        }
        return tempLogMsg;
    }

    /**
     * password regex
     *
     * @param logMsg original log
     */
    static String passwordHandler(Pattern pwdPattern, String logMsg) {

        Matcher matcher = pwdPattern.matcher(logMsg);

        StringBuffer sb = new StringBuffer(logMsg.length());

        while (matcher.find()) {

            String password = matcher.group();

            String maskPassword = StringUtils.repeat(Constants.STAR, StringUtils.length(password));

            matcher.appendReplacement(sb, maskPassword);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }


}
