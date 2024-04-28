package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum DoCheckpointType {
    /**
     * start not from checkpoint
     */
    CHECKPOINT(0, "start not from checkpoint"),

    /**
     * start from last checkpoint address
     */
    SAVEPOINT(1, "start from last checkpoint");

    @EnumValue
    Integer key;
    String value;

    DoCheckpointType(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
