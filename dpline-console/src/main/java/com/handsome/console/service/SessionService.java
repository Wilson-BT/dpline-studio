package com.handsome.console.service;



import com.handsome.dao.entity.Session;
import com.handsome.dao.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * session service
 */
public interface SessionService {

    /**
     * get user session from request
     *
     * @param request request
     * @return session
     */
    Session getSession(HttpServletRequest request);

    /**
     * create session
     *
     * @param user user
     * @param ip ip
     * @return session string
     */
    String createSession(User user, String ip);

    /**
     * sign out
     * remove ip restrictions
     *
     * @param ip   no use
     * @param loginUser login user
     */
    void signOut(String ip, User loginUser);
}
