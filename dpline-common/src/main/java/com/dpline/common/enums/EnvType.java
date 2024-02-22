package com.dpline.common.enums;


import java.util.Optional;

public enum EnvType {
    TEST(0,"test"),
    PROD(1,"prod"),
    DEV(2,"dev");

    private Integer key;
    private String value;

    EnvType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Optional<EnvType> of(String type){
        for (EnvType value: EnvType.values()){
            if(value.getValue().equals(type)){
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
