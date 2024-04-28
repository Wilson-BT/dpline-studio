package com.dpline.console.controller;


import com.dpline.common.Constants;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Result;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static com.dpline.common.Constants.*;

public abstract class BaseController {

    public Result<Object> checkPageParams(int page, int pageSize) {
        Result<Object> result = new Result<>();
        Status resultEnum = Status.SUCCESS;
        String msg = Status.SUCCESS.getMsg();
        if (page <= 0) {
            resultEnum = Status.REQUEST_PARAMS_NOT_VALID_ERROR;
            msg = MessageFormat.format(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), Constants.PAGE_NUMBER);
        } else if (pageSize <= 0) {
            resultEnum = Status.REQUEST_PARAMS_NOT_VALID_ERROR;
            msg = MessageFormat.format(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), Constants.PAGE_SIZE);
        }
        result.setCode(resultEnum.getCode());
        result.setMsg(msg);
        return result;
    }


    public Result<Object> returnDataList(Map<String, Object> result) {
        Status status = (Status) result.get(Constants.STATUS);
        if (status == Status.SUCCESS) {
            String msg = Status.SUCCESS.getMsg();
            Object datalist = result.get(Constants.DATA_LIST);
            return success(msg, datalist);
        } else {
            Integer code = status.getCode();
            String msg = (String) result.get(Constants.MSG);
            return error(code, msg);
        }
    }


    /**
     * success
     *
     * @return success result code
     */
    public Result success() {
        Result result = new Result();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(Status.SUCCESS.getMsg());

        return result;
    }


    /**
     * return data no paging
     *
     * @param msg success message
     * @param list data list
     * @return success result code
     */
    public Result<Object> success(String msg, Object list) {
        return getResult(msg, list);
    }

    /**
     * return data no paging
     *
     * @param list success
     * @return success result code
     */
    public Result success(Object list) {
        return getResult(Status.SUCCESS.getMsg(), list);
    }

    /**
     * return the data use Map format, for example, passing the value of key, value, passing a value
     * eg. "/user/add"  then return user name: zhangsan
     *
     * @param msg message
     * @param object success object data
     * @return success result code
     */
    public Result success(String msg, Map<String, Object> object) {
        return getResult(msg, object);
    }

    /**
     * return data with paging
     *
     * @param totalList success object list
     * @param currentPage current page
     * @param total total
     * @param totalPage total page
     * @return success result code
     */
    public Result success(Object totalList, Integer currentPage,
                          Integer total, Integer totalPage) {
        Result result = new Result();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(Status.SUCCESS.getMsg());

        Map<String, Object> map = new HashMap<>(8);
        map.put(Constants.TOTAL_LIST, totalList);
        map.put(Constants.CURRENT_PAGE, currentPage);
        map.put(Constants.TOTAL_PAGE, totalPage);
        map.put(Constants.TOTAL, total);
        result.setData(map);
        return result;
    }

    /**
     * error handle
     *
     * @param code result code
     * @param msg result message
     * @return error result code
     */
    public Result error(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    /**
     * put message to map
     *
     * @param result result
     * @param status status
     * @param statusParams object messages
     */
    protected void putMsg(Map<String, Object> result, Status status, Object... statusParams) {
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
     * @param result result
     * @param status status
     * @param statusParams status parameters
     */
    protected void putMsg(Result result, Status status, Object... statusParams) {
        result.setCode(status.getCode());

        if (statusParams != null && statusParams.length > 0) {
            result.setMsg(MessageFormat.format(status.getMsg(), statusParams));
        } else {
            result.setMsg(status.getMsg());
        }

    }

    /**
     * get result
     *
     * @param msg message
     * @param list object list
     * @return result code
     */
    private Result<Object> getResult(String msg, Object list) {
        Result<Object> result = new Result<>();
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(msg);

        result.setData(list);
        return result;
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader(HTTP_X_FORWARDED_FOR);

        if (StringUtils.isNotEmpty(clientIp) && !clientIp.equalsIgnoreCase(HTTP_HEADER_UNKNOWN)) {
            int index = clientIp.indexOf(COMMA);
            if (index != -1) {
                return clientIp.substring(0, index);
            } else {
                return clientIp;
            }
        }

        clientIp = request.getHeader(HTTP_X_REAL_IP);
        if (StringUtils.isNotEmpty(clientIp) && !clientIp.equalsIgnoreCase(HTTP_HEADER_UNKNOWN)) {
            return clientIp;
        }

        return request.getRemoteAddr();
    }
}
