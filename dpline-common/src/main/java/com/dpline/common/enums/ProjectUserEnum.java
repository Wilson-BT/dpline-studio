package com.dpline.common.enums;

public enum ProjectUserEnum {
    /**
     * 项目用户角色
     * 2：项目责任人
     * 1：项目管理者
     * 0：普通用户
     */
    ORGANIZE_USER(1),
    GENERAL_USER(0);

    ProjectUserEnum(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }
}
