package com.dpline.console.security;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * authentication type
 */
public enum AuthenticationType {

    PASSWORD(0, "verify via user name and password"),
    LDAP(1, "verify via LDAP server");

    AuthenticationType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumValue
    private final int code;
    private final String desc;
}
