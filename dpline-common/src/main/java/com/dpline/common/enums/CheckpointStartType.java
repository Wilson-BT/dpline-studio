package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CheckpointStartType {

    /**
     * start not from checkpoint
     */
    START_NO_CHECKPOINT(0, "start not from checkpoint"),

    /**
     * start from last checkpoint address
     */
    FROM_LAST_CHECKPOINT(1, "start from last checkpoint"),

    /**
     * start from one save point path
     */
    FROM_EXISTS_SAVEPOINT(2, "start from last checkpoint"),
    /**
     * start from spec savepoint
     */
    FROM_SPEC_SAVEPOINT(3, "start from spec savepoint");

    @EnumValue
    Integer key;
    String value;

    CheckpointStartType(int key, String value) {
        this.key = key;
        this.value = value;
    }

}
