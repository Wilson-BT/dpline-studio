package com.dpline.console.security.impl.pwd;


import com.dpline.console.security.impl.AbstractAuthenticator;
import com.dpline.console.service.impl.UsersServiceImpl;
import com.dpline.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordAuthenticator extends AbstractAuthenticator {
    @Autowired
    private UsersServiceImpl userService;

    @Override
    public User login(String userCode, String password, String extra) {
        return userService.queryUser(userCode, password);
    }
}
