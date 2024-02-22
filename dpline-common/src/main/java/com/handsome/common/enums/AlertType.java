package com.handsome.common.enums;

import java.util.Optional;

public enum AlertType {
    NONE(0,"none"),

    EMAIL(1,"email"),

    HTTP(2,"http"),

    WECOM(3,"wecom");

    private final int key;

    private final String value;

    AlertType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public Optional<AlertType> of(int key){
        for (AlertType alertType : AlertType.values()) {
            if (alertType.key == key){
                return Optional.of(alertType);
            }
        }
        return Optional.empty();
    }
}
