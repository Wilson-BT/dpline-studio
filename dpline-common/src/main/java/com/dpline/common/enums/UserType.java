package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Optional;

/**
 * user type
 */
public enum UserType {
    /**
     * 1 admin user; 0 general user
     */
    GENERAL_USER(0, "general user"),

    ADMIN_USER(1, "admin user");


    UserType(int code, String descp){
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final int code;

    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static Optional<UserType> of(Integer code){
        for (UserType value:UserType.values()) {
            if(value.code == code){
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}

