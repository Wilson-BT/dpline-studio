package com.dpline.common.enums;

import cn.hutool.core.util.StrUtil;

public enum DataSourceType {
    MYSQL(1, "mysql");

    private Integer code;

    private String name;

    DataSourceType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static DataSourceType getType(String name) {
        DataSourceType dataSourceType = null;
        if (StrUtil.isBlank(name)) {
            return dataSourceType;
        }

        for (DataSourceType temp : values()) {
            if (temp.getName().equals(name)) {
                dataSourceType = temp;
                break;
            }
        }
        return dataSourceType;
    }
}
