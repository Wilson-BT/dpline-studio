package com.dpline.k8s.operator.config;

import com.dpline.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // get token
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token) || !token.equals(Constants.MD5_TOKEN)) {
            return false;
        }

        return true;
    }
}
