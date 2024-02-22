package com.handsome.common.enums;

public enum OperatorType {
    SQL_VERIFY(0,"sql verify"),
    SQL_RUN(1,"task run"),
    SQL_STOP(2,"task stop"),
    UPDATE_CONFIG(3,"update config"),
    TASK_GRAPH(4,"get task graph");

    private int key;
    private String value;

    OperatorType(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
