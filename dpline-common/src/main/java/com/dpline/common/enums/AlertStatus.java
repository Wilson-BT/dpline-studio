
package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * alert status
 */
public enum AlertStatus {
    /**
     * 0 waiting executed; 1 execute successfully，2 execute failed
     */
    WAIT_EXECUTION(0, "waiting executed"),
    EXECUTION_SUCCESS(1, "execute successfully"),
    EXECUTION_FAILURE(2, "execute failed");


    AlertStatus(int code, String descp){
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
