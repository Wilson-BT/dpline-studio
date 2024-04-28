package com.dpline.common.enums;

import java.util.Optional;

public enum AlertMode {

    NONE(0,"NONE"),

    STOPPED(1,"STOPPED"),

    RUNNING(2,"RUNNING"),

    ALL(3,"ALL");

    private final int key;

    private final String value;

    AlertMode(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Optional<AlertMode> of(int key){
        for (AlertMode alertMode : AlertMode.values()) {
            if (alertMode.key == key){
                return Optional.of(alertMode);
            }
        }
        return Optional.empty();
    }
    public static AlertMode of(String key){
        for (AlertMode alertMode : AlertMode.values()) {
            if (alertMode.value.equals(key)){
                return alertMode;
            }
        }
        return null;
    }
}
