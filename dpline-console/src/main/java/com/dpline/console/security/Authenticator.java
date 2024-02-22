package com.dpline.console.security;


import com.dpline.dao.entity.User;
import com.dpline.common.util.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface Authenticator {
    /**
     * Verifying legality via username and password
     * @param username user name
     * @param password user password
     * @param extra extra info
     * @return result object
     */
    Result<Map<String, String>> authenticate(String username, String password, String extra);

    /**
     * Get authenticated user
     * @param request http servlet request
     * @return user
     */
    User getAuthUser(HttpServletRequest request);
}
