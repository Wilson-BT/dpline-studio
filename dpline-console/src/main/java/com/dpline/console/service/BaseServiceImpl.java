package com.dpline.console.service;

import com.dpline.common.Constants;
import com.dpline.common.enums.Status;
import com.dpline.common.enums.UserType;
import com.dpline.common.util.DateUtils;
import com.dpline.common.util.Result;
import com.dpline.dao.entity.User;
import com.dpline.dao.generic.Pagination;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * base service impl
 */
public class BaseServiceImpl implements BaseService {

    /**
     * check admin
     *
     * @param user input user
     * @return ture if administrator, otherwise return false
     */
    @Override
    public boolean isAdmin(User user) {
        return user.getIsAdmin() == UserType.ADMIN_USER.getCode();
    }

    /**
     * isNotAdmin
     *
     * @param loginUser login user
     * @param result result code
     * @return true if not administrator, otherwise false
     */
    @Override
    public boolean isNotAdmin(User loginUser, Map<String, Object> result) {
        //only admin can operate
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return true;
        }
        return false;
    }

    public boolean isNotAdmin(User loginUser, Result<Object> result) {
        //only admin can operate
        if (!isAdmin(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return true;
        }
        return false;
    }

    /**
     * put message to map
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    @Override
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
    @Override
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
    @Override
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
     * has perm
     *
     * @param operateUser operate user
     * @param createUserId create user id
     */
    @Override
    public boolean hasPerm(User operateUser, int createUserId) {
        return operateUser.getId() == createUserId || isAdmin(operateUser);
    }

    /**
     * check and parse date parameters
     *
     * @param startDateStr start date string
     * @param endDateStr end date string
     * @return map<status,startDate,endDate>
     */
    @Override
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

    public static Pagination pageHelper(List list, Integer pageNum, Integer pageSize) {
        Pagination instance = Pagination.getInstance(pageNum,pageSize);
        int total = list.size();
        instance.setRowTotal(total);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        if (startIndex > endIndex) {
            instance.setRows(new ArrayList<>());
            return instance;
        } else {
            instance.setRows(list.subList(startIndex, endIndex));
            return instance;
        }
    }

}
