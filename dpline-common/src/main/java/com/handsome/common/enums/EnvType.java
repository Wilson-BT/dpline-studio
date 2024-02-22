package com.handsome.common.enums;

public enum EnvType {
    TEST(0,"test"),
    PROD(1,"prod");

    private Integer key;
    private String value;

    EnvType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
}
