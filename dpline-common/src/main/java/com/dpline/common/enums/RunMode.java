package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum RunMode {
    LOCAL("local"),
    APPLICATION("application"),
    SESSION("session"),
    REMOTE("remote");

    @EnumValue
    private String value;

    RunMode(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public static RunMode of(String value){
        for(RunMode runMode : RunMode.values()){
            if(runMode.getValue().equals(value)){
                return runMode;
            }
        }
        return null;
    }
}
