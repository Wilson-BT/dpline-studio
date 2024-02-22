package com.dpline.console.util;


import com.dpline.dao.entity.User;

public class Context {

    private String userCode;
    private User user;

    public String getUserCode() {
        return userCode;
    }
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (this.user != null) {
            this.userCode = this.user.getUserCode();
        }
    }

}
