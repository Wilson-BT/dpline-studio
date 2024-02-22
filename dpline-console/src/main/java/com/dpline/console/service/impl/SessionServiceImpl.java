package com.dpline.console.service.impl;

import com.dpline.console.controller.BaseController;
import com.dpline.console.service.BaseServiceImpl;
import com.dpline.console.service.SessionService;
import com.dpline.common.Constants;
import com.dpline.dao.entity.Session;
import com.dpline.dao.entity.User;
import com.dpline.dao.mapper.SessionMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * session service implement
 */
@Service
public class SessionServiceImpl extends BaseServiceImpl implements SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    @Autowired
    private SessionMapper sessionMapper;

    /**
     * get user session from request
     *
     * @param request request
     * @return session
     */
    @Override
    public Session getSession(HttpServletRequest request) {
        String sessionId = request.getHeader(Constants.SESSION_ID);

        if (StringUtils.isBlank(sessionId)) {
            Cookie cookie = WebUtils.getCookie(request, Constants.SESSION_ID);

            if (cookie != null) {
                sessionId = cookie.getValue();
            }
        }

        if (StringUtils.isBlank(sessionId)) {
            return null;
        }

        String ip = BaseController.getClientIpAddress(request);
        logger.debug("get session: {}, ip: {}", sessionId, ip);

        return sessionMapper.selectById(sessionId);
    }

    /**
     * 创建session，
     *
     * @param user user
     * @param ip ip
     * @return session string
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String createSession(User user, String ip) {
        Session session = null;

        // logined
        List<Session> sessionList = sessionMapper.queryByUserId(user.getId());

        Date now = new Date();

        /**
         * if you have logged in and are still valid, return directly
         * 如果同时有多个session，多平台访问的话，需要删除其他平台的session，然后更新session最新的登录时间
         */
        if (CollectionUtils.isNotEmpty(sessionList)) {
            // is session list greater 1 ， delete other ，get one
            if (sessionList.size() > 1) {
                for (int i = 1; i < sessionList.size(); i++) {
                    sessionMapper.deleteById(sessionList.get(i).getId());
                }
            }
            session = sessionList.get(0);
            if (now.getTime() - session.getLastLoginTime().getTime() <= Constants.SESSION_TIME_OUT * 1000) {
                /**
                 * updateProcessInstance the latest login time
                 */
                session.setLastLoginTime(now);
                sessionMapper.updateById(session);

                return session.getId();

            } else {
                /**
                 * session expired, then delete this session first
                 */
                sessionMapper.deleteById(session.getId());
            }
        }

        // assign new session
        session = new Session();

        session.setId(UUID.randomUUID().toString());
        session.setIp(ip);
        session.setUserId(user.getId());
        session.setLastLoginTime(now);

        sessionMapper.insert(session);

        return session.getId();
    }

    /**
     * sign out
     * remove ip restrictions
     *
     * @param ip no use
     * @param loginUser login user
     */
    @Override
    public void signOut(String ip, User loginUser) {
        try {
            /**
             * query session by user id and ip
             */
            Session session = sessionMapper.queryByUserIdAndIp(loginUser.getId(), ip);

            //delete session
            sessionMapper.deleteById(session.getId());
        } catch (Exception e) {
            logger.warn("userId : {} , ip : {} , find more one session", loginUser.getId(), ip);
        }
    }

}
