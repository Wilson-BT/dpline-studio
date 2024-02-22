package com.handsome.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;

/**
 * 运行模式，目前有三种
 */
public enum RunModeType {
    /**
     *  local模式
     */
    LOCAL(0, "local"),
    /**
     * K8S_APPLICATION
     */
    K8S_APPLICATION(1, "kubernetes-application"),
    /**
     * K8S_SESSION 模式
     */
    K8S_SESSION(2, "kubernetes-session"),

    /**
     * REMOTE 提交到standalone集群中，如docker 启动的
     */
    REMOTE(3,"remote");

    @EnumValue
    private final int key;

    private final String value;

    RunModeType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static RunModeType of(int key) {
        return Arrays.stream(RunModeType.values())
                .filter((en) -> en.getKey() == key)
                .findFirst()
                .orElse(null);
    }
}
