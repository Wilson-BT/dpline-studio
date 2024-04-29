package com.dpline.k8s.operator.service;

import com.dpline.common.Constants;
import com.dpline.common.enums.Status;
import com.dpline.common.util.DateUtils;
import com.dpline.common.util.Result;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * base service impl
 */
public class BaseService {


    /**
     * put message to map
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    public void putMsg(Map<String, Object> result, Status status, Object... statusParams) {
        result.put(Constants.STATUS, status);
        if (statusParams != null && statusParams.length > 0) {
            result.put(Constants.MSG, MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.put(Constants.MSG, status.getMsg());
        }
    }

    /**
     * put message to result object
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    public void putMsg(Result<Object> result, Status status, Object... statusParams) {
        result.setCode(status.getCode());
        if (statusParams != null && statusParams.length > 0) {
            result.setMsg(MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.setMsg(status.getMsg());
        }
    }

    /**
     * check
     *
     * @param result result
     * @param bool bool
     * @param userNoOperationPerm status
     * @return check result
     */
    public boolean check(Map<String, Object> result, boolean bool, Status userNoOperationPerm) {
        // only admin can operate
        if (bool) {
            result.put(Constants.STATUS, userNoOperationPerm);
            result.put(Constants.MSG, userNoOperationPerm.getMsg());
            return true;
        }
        return false;
    }

    /**
     * check and parse date parameters
     *
     * @param startDateStr start date string
     * @param endDateStr end date string
     * @return map<status,startDate,endDate>
     */
    public Map<String, Object> checkAndParseDateParameters(String startDateStr, String endDateStr) {
        Map<String, Object> result = new HashMap<>();
        Date start = null;
        if (!StringUtils.isEmpty(startDateStr)) {
            start = DateUtils.getScheduleDate(startDateStr);
            if (Objects.isNull(start)) {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, Constants.START_END_DATE);
                return result;
            }
        }
        result.put(Constants.START_TIME, start);

        Date end = null;
        if (!StringUtils.isEmpty(endDateStr)) {
            end = DateUtils.getScheduleDate(endDateStr);
            if (Objects.isNull(end)) {
                putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, Constants.START_END_DATE);
                return result;
            }
        }
        result.put(Constants.END_TIME, end);

        putMsg(result, Status.SUCCESS);
        return result;
    }

}
