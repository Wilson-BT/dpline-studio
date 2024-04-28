package com.dpline.console.util;

import com.dpline.common.Constants;
import com.dpline.common.enums.Status;
import com.dpline.common.enums.UserType;
import com.dpline.common.util.JSONUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * check utils
 */
public class CheckUtils {

    private CheckUtils() {
        throw new IllegalStateException("CheckUtils class");
    }

    /**
     * check username
     *
     * @param userName user name
     * @return true if user name regex valid,otherwise return false
     */
    public static boolean checkUserName(String userName) {
        return regexChecks(userName, Constants.REGEX_USER_NAME);
    }

    /**
     * check email
     *
     * @param email email
     * @return true if email regex valid, otherwise return false
     */
    public static boolean checkEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        return email.length() > 5 && email.length() <= 40 && regexChecks(email, Constants.REGEX_MAIL_NAME);
    }


    /**
     * check extra info
     *
     * @param otherParams other parames
     * @return true if other parameters are valid, otherwise return false
     */
    public static boolean checkOtherParams(String otherParams) {
        return !StringUtils.isEmpty(otherParams) && !JSONUtils.checkJsonValid(otherParams);
    }

    /**
     * check password
     *
     * @param password password
     * @return true if password regex valid, otherwise return false
     */
    public static boolean checkPassword(String password) {
        return !StringUtils.isEmpty(password) && password.length() >= 2 && password.length() <= 20;
    }

    /**
     * check phone phone can be empty.
     *
     * @param phone phone
     * @return true if phone regex valid, otherwise return false
     */
    public static boolean checkPhone(String phone) {
        return StringUtils.isEmpty(phone) || phone.length() == 11;
    }

    /**
     * check time zone parameter
     * @param timeZone timeZone
     * @return true if timeZone is valid, otherwise return false
     */
    public static boolean checkTimeZone(String timeZone) {
        try {
            ZoneId.of(timeZone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * regex check
     *
     * @param str     input string
     * @param pattern regex pattern
     * @return true if regex pattern is right, otherwise return false
     */
    private static boolean regexChecks(String str, Pattern pattern) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        return pattern.matcher(str).matches();
    }

    /**
     * check project description
     *
     * @param desc desc
     * @return true if description regex valid, otherwise return false
     */
    public static Map<String, Object> checkDesc(String desc) {
        Map<String, Object> result = new HashMap<>();
        if (!StringUtils.isEmpty(desc) && desc.length() > 200) {
            result.put(Constants.STATUS, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
            result.put(Constants.MSG,
                    MessageFormat.format(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), "desc length"));
        } else {
            result.put(Constants.STATUS, Status.SUCCESS);
        }
        return result;
    }

    public static boolean checkUserType(int isAdmin) {
        Optional<UserType> of = UserType.of(isAdmin);
        return of.isPresent();
    }
}
