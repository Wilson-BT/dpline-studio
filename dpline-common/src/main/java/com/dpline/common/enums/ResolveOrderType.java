package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ResolveOrderType {

    PARENT_FIRST(0,"parent-first"),

    CHILD_FIRST(1,"child-first");

    @EnumValue
    int key;
    String value;
    ResolveOrderType(int key,String value){
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
