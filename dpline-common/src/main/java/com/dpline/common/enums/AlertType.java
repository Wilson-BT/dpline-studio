package com.dpline.common.enums;

import java.util.Optional;

public enum AlertType {
    NONE(0,"NONE"),

    EMAIL(1,"EMAIL"),

    HTTP(2,"HTTP"),

    WECOM(3,"WECOM");

    private final int key;

    private final String value;

    AlertType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Optional<AlertType> of(int key){
        for (AlertType alertType : AlertType.values()) {
            if (alertType.key == key){
                return Optional.of(alertType);
            }
        }
        return Optional.empty();
    }
    public static Optional<AlertType> of(String key){
        for (AlertType alertType : AlertType.values()) {
            if (alertType.value.equals(key)){
                return Optional.of(alertType);
            }
        }
        return Optional.empty();
    }
}
