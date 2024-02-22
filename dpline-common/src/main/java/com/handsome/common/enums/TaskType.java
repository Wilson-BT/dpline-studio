package com.handsome.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 *  Task type:
 *      Sql or CustomCode
 */
public enum TaskType {

    SQL(0,"sql"),

    CUSTOM_CODE(1,"customCode");

    @EnumValue
    int key;

    String value;

    TaskType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
