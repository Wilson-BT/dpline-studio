package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * have_script
 * have_file
 * can_retry
 * have_arr_variables
 * have_map_variables
 * have_alert
 */
public enum Flag {
    /**
     * 0 no
     * 1 yes
     * 2 no status
     */
    NO(0, "no"),
    YES(1, "yes"),
    MID(2,"middle"),
    FAIL(3,"failed");

    Flag(int code, String descp) {
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
}
