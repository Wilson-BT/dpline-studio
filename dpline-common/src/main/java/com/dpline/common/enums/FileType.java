package com.dpline.common.enums;

import java.util.Arrays;

public enum FileType {
    /**
     * 作业类型:SQL
     */
    SQL_STREAM("SQL"),
    /**
     * 作业类型:DS
     */
    DATA_STREAM("DS");

    private String type;

    FileType(String type){this.type = type;};

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public static FileType of(String key){
        return Arrays.stream(FileType.values())
            .filter((en) -> en.getType().equals(key))
            .findFirst()
            .orElse(null);
    }
}
