package com.dpline.console.service;


import com.dpline.common.enums.Status;
import com.dpline.common.util.Result;
import com.dpline.dao.entity.User;

import java.util.Map;

/**
 * base service
 */
public interface BaseService {

    /**
     * check admin
     *
     * @param user input user
     * @return ture if administrator, otherwise return false
     */
    boolean isAdmin(User user);

    /**
     * isNotAdmin
     *
     * @param loginUser login user
     * @param result result code
     * @return true if not administrator, otherwise false
     */
    boolean isNotAdmin(User loginUser, Map<String, Object> result);

    /**
     * put message to map
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    void putMsg(Map<String, Object> result, Status status, Object... statusParams);

    /**
     * put message to result object
     *
     * @param result result code
     * @param status status
     * @param statusParams status message
     */
    void putMsg(Result<Object> result, Status status, Object... statusParams);

    /**
     * check
     *
     * @param result result
     * @param bool bool
     * @param userNoOperationPerm status
     * @return check result
     */
    boolean check(Map<String, Object> result, boolean bool, Status userNoOperationPerm);

    /**
     * has perm
     *
     * @param operateUser operate user
     * @param createUserId create user id
     */
    boolean hasPerm(User operateUser, int createUserId);

    /**
     * check and parse date parameters
     *
     * @param startDateStr start date string
     * @param endDateStr end date string
     * @return map<status,startDate,endDate>
     */
    Map<String, Object> checkAndParseDateParameters(String startDateStr, String endDateStr);
}
