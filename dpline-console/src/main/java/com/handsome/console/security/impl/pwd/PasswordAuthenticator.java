package com.handsome.console.security.impl.pwd;


import com.handsome.dao.entity.User;
import com.handsome.console.security.impl.AbstractAuthenticator;
import com.handsome.console.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordAuthenticator extends AbstractAuthenticator {
    @Autowired
    private UsersService userService;

    @Override
    public User login(String userCode, String password, String extra) {
        return userService.queryUser(userCode, password);
    }
}
