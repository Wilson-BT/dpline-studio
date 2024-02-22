package com.dpline.console.security.impl;


import com.dpline.common.Constants;
import com.dpline.common.enums.Flag;
import com.dpline.common.enums.Status;
import com.dpline.common.util.Result;
import com.dpline.console.security.Authenticator;
import com.dpline.console.service.SessionService;
import com.dpline.console.service.impl.UsersServiceImpl;
import com.dpline.dao.entity.Session;
import com.dpline.dao.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAuthenticator implements Authenticator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAuthenticator.class);

    @Autowired
    private UsersServiceImpl userService;

    @Autowired
    private SessionService sessionService;

    /**
     * 用户登录
     * 创建或更新session
     * 返回sessionId
     *
     * @param userName
     * @param password
     * @param extra
     * @return user object in databse
     */
    public abstract User login(String userName, String password, String extra);

    @Override
    public Result<Map<String, String>> authenticate(String userCode, String password, String extra) {
        Result<Map<String, String>> result = new Result<>();
        User user = login(userCode, password, extra);
        if (user == null) {
            result.setCode(Status.USER_NAME_PASSWD_ERROR.getCode());
            result.setMsg(Status.USER_NAME_PASSWD_ERROR.getMsg());
            return result;
        }

        // check user state
        if (user.getEnabledFlag() == Flag.NO.ordinal()) {
            result.setCode(Status.USER_DISABLED.getCode());
            result.setMsg(Status.USER_DISABLED.getMsg());
            return result;
        }

        // create session
        String sessionId = sessionService.createSession(user, extra);
        if (sessionId == null) {
            result.setCode(Status.LOGIN_SESSION_FAILED.getCode());
            result.setMsg(Status.LOGIN_SESSION_FAILED.getMsg());
            return result;
        }
        logger.info("sessionId : {}", sessionId);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(Constants.SESSION_ID, sessionId);
//        resultMap.put(Constants.SESSION_USER_TYPE, String.valueOf(user.getIsAdmin()));
//        resultMap.put(Constants.SESSION_USER_ID, String.valueOf(user.getId()));
//        resultMap.put(Constants.SESSION_USER_CODE, user.getUserCode());
//        resultMap.put(Constants.SESSION_USER_NAME, UriEncoder.encode(user.getUserCode()));
        result.setData(resultMap);
        result.setCode(Status.SUCCESS.getCode());
        result.setMsg(Status.LOGIN_SUCCESS.getMsg());
        return result;
    }

    @Override
    public User getAuthUser(HttpServletRequest request) {
        Session session = sessionService.getSession(request);
//        Date now = new Date();
        if (session == null) {
            logger.info("session info is null ");
            return null;
        }
//      TODO判断session的时间是否超时
//        if (now.getTime() - session.getLastLoginTime().getTime() >= Constants.SESSION_TIME_OUT * 1000){
//            logger.info("user session id is expired time out of {}ms",Constants.SESSION_TIME_OUT * 1000);
//            return null;
//        }
        //get user object from session
        return userService.queryUser(session.getUserId());
    }

}
