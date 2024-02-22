package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum DraftTagType {

    TASK_TAG(0,"task tag"),
    TASK_DEFINITION(1,"task definition");

    @EnumValue
    int key;
    String value;
    DraftTagType(int key,String value){
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
