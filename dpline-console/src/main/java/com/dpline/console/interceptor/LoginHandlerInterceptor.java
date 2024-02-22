package com.dpline.console.interceptor;

import com.dpline.common.Constants;
import com.dpline.common.enums.Flag;
import com.dpline.common.enums.Status;
import com.dpline.common.util.JSONUtils;
import com.dpline.common.util.Result;
import com.dpline.console.security.Authenticator;
import com.dpline.console.util.Context;
import com.dpline.console.util.ContextUtils;
import com.dpline.dao.entity.User;
import com.dpline.dao.mapper.UserMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * login interceptor, must log in first
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandlerInterceptor.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Authenticator authenticator;

    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return boolean true or false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // get token
        String token = request.getHeader("token");
        User user;
        if (StringUtils.isEmpty(token)) {
            user = authenticator.getAuthUser(request);
            // if user is null
            if (user == null) {
                setResponseBody(response);
                logger.info("user does not exist");
                return false;
            }
        } else {
            user = userMapper.queryUserByToken(token);
            if (user == null) {
                setResponseBody(response);
                logger.info("user token has expired");
                return false;
            }
        }

        // check user state
        if (user.getEnabledFlag() == Flag.NO.ordinal()) {
            setResponseBody(response);
            logger.info(Status.USER_DISABLED.getMsg());
            return false;
        }

        Context context = new Context();
        context.setUser(user);
        ContextUtils.set(context);

        request.setAttribute(Constants.SESSION_USER, user);
        return true;
    }

    void setResponseBody(HttpServletResponse response){
        response.setStatus(HttpStatus.SC_OK);
        try {
            response.getWriter().write(JSONUtils.toJsonString(Result.error(Status.AUTHENTICATION_FAILED)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextUtils.unset();
    }

}
