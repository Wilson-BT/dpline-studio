package com.dpline.console.interceptor;


import com.dpline.common.Constants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 国际化,动态改变语言
 */
public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie cookie = WebUtils.getCookie(request, Constants.LOCALE_LANGUAGE);
        if (cookie != null) {
            // Proceed in cookie
            return true;
        }
        // Proceed in header
        String newLocale = request.getHeader(Constants.LOCALE_LANGUAGE);
        if (newLocale != null) {
            LocaleContextHolder.setLocale(parseLocaleValue(newLocale));
        }
        return true;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }

}
