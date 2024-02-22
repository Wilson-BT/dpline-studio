package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum RestartStrategy {

    NONE(0,"none"),

    FIXEDDELAY(1,"fixed-delay");

    RestartStrategy(int code, String value) {
        this.code = code;
        this.value = value;
    }

    @EnumValue
    private final int code;
    private final String value;

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
