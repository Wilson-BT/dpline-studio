package com.handsome.console.controller;

import com.handsome.common.Constants;
import com.handsome.common.enums.Status;
import com.handsome.console.aspect.AccessLogAnnotation;
import com.handsome.dao.entity.User;
import com.handsome.console.exception.ApiException;
import com.handsome.console.security.Authenticator;
import com.handsome.console.service.SessionService;
import com.handsome.common.util.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.handsome.common.enums.Status.*;


/**
 * login controller
 */
@RestController
@RequestMapping("auth")
public class LoginController extends BaseController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Authenticator authenticator;

    /**
     * login
     *
     * @param userCode user name
     * @param password user password
     * @param request request
     * @param response response
     * @return login result
     */
    @PostMapping(value = "/login")
    @ApiException(USER_LOGIN_FAILURE)
    @AccessLogAnnotation(ignoreRequestArgs = {"password", "request", "response"})
    public Result login(@RequestParam(value = "userCode") String userCode,
                        @RequestParam(value = "password") String password,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        //user name check
        if (StringUtils.isEmpty(userCode)) {
            return Result.error(Status.USER_NAME_NULL);
        }

        // user ip check
        String ip = getClientIpAddress(request);
        if (StringUtils.isEmpty(ip)) {
            return Result.error(Status.IP_IS_EMPTY);
        }

        // 校验用户名和密码的正确性
        Result<Map<String, String>> result = authenticator.authenticate(userCode, password, ip);
        if (result.getCode() != Status.SUCCESS.getCode()) {
            return result;
        }
        response.setStatus(HttpStatus.SC_OK);
        Map<String, String> cookieMap = result.getData();
        for (Map.Entry<String, String> cookieEntry : cookieMap.entrySet()) {
            Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        return result;
    }

    /**
     * sign out
     *
     * @param loginUser login user
     * @param request request
     * @return sign out result
     */
    @PostMapping(value = "/signOut")
    @ApiException(SIGN_OUT_ERROR)
    @AccessLogAnnotation(ignoreRequestArgs = {"loginUser", "request"})
    public Result signOut(@ApiIgnore  @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                          HttpServletRequest request) {
        String ip = getClientIpAddress(request);
        sessionService.signOut(ip, loginUser);
        //clear session
        request.removeAttribute(Constants.SESSION_USER);
        return success();
    }
}
