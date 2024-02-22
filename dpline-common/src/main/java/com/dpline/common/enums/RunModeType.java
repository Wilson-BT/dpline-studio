package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;

/**
 * 运行模式，目前有三种
 */
public enum RunModeType {
    /**
     *  local模式
     */
    LOCAL(0, "local",
        ClusterType.LOCAL,
        RunMode.LOCAL),
    /**
     * K8S_APPLICATION
     */
    K8S_APPLICATION(1,
        "kubernetes-application",
        ClusterType.KUBERNETES,
        RunMode.APPLICATION),
    /**
     * K8S_SESSION 模式
     */
    K8S_SESSION(2, "kubernetes-session",
        ClusterType.KUBERNETES,
        RunMode.SESSION),

    /**
     * K8S_APPLICATION
     */
    YARN_APPLICATION(1, "kubernetes-application",
        ClusterType.YARN,
        RunMode.APPLICATION),
    /**
     * K8S_SESSION 模式
     */
    YARN_SESSION(2, "kubernetes-session",
        ClusterType.YARN,
        RunMode.SESSION),

    /**
     * REMOTE 提交到standalone集群中，如docker 启动的
     */
    REMOTE(3,"remote",
        ClusterType.REMOTE,
        RunMode.REMOTE);


    @EnumValue
    private final int key;

    private final String value;

    private final ClusterType clusterType;

    private final RunMode runMode;

    RunModeType(int key, String value,ClusterType clusterType,RunMode runMode) {
        this.key = key;
        this.value = value;
        this.clusterType = clusterType;
        this.runMode = runMode;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public RunMode getRunMode() {
        return runMode;
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

    public static RunModeType of(String value) {
        return Arrays.stream(RunModeType.values())
            .filter((en) -> en.name().equals(value))
            .findFirst()
            .orElse(null);
    }

    public static RunModeType parseRunModeType(ClusterType clusterType,RunMode runMode){
        return Arrays.stream(RunModeType.values())
            .filter((en) -> {
                return en.getClusterType().equals(clusterType) && en.getRunMode().equals(runMode);
            })
            .findFirst()
            .orElse(null);
    }
}
