package com.dpline.common.enums;

public enum LocalTaskOperateType {
    SQL_VERIFY(0,"sql verify"),
    SQL_RUN(1,"task run"),
    SQL_STOP(2,"task stop"),
    UPDATE_CONFIG(3,"update config"),
    TASK_GRAPH(4,"get task graph");

    private int key;
    private String value;

    LocalTaskOperateType(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
