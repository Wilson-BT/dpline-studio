package com.dpline.common.enums;

public enum StreamType {
    STREAM(0,"stream mode"),
    BATCH(1,"batch mode");
    private int key;
    private String value;

    StreamType(int key, String value) {
        this.key = key;
        this.value = value;
    }
}
