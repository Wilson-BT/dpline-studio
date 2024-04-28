package com.dpline.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.*;

/**
 * running status for workflow and task nodes
 */
public enum ExecStatus {

    /**
     * status：
     * 项目打包比如sql => 项目上传(BUILD成功) => 项目 => 提交任务/加入缓存 => 缓存任务监控 jobManager 生成 => taskManager 生成 => RUNNING
     *
     *
     * FAILURE 步骤失败则失败，给出失败原因
     * KILLED 在远程被kill掉
     * CANCELLED 在平台被stop
     *
     * sql/jar包、选择直接挂载
     *
     */
    SUBMITTING(0, "submit success"),
    INITIALIZING(1,"k8s initing"),
    RUNNING(2, "task running"),
    CANCELLING(3, "task cancelling"),
    CANCELED(4,"task cancelled"),
    STOPPING(5,"task stopping"),
    STOPPED(6,"task stopped"),
    FAILING(7, "being failure"),
    FAILED(8, "being failure"),
    FINISHED(9, "finished"),
    TERMINATED(10,"Terminated"),
    NONE(11, "no status");


    ExecStatus(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final int code;
    private final String descp;

    private static HashMap<Integer, ExecStatus> EXECUTION_STATUS_MAP = new HashMap<>();

    static {
        for (ExecStatus executionStatus : ExecStatus.values()) {
            EXECUTION_STATUS_MAP.put(executionStatus.code, executionStatus);
        }
    }

    /**
     * status is success
     *
     * @return status
     */
    public boolean typeIsRunSuccess() {
        return this == RUNNING;
    }

    /**
     * status is failure
     *
     * @return status
     */
    public boolean typeIsFailure() {
        return this == FAILING || this == FAILED;
    }

    /**
     * status is pause
     *
     * @return status
     */
    public boolean isStopped() {
        return this == CANCELED || this == FAILED || this == FINISHED || this == TERMINATED || this == STOPPED;
    }

    public boolean isStopping(){
        return this == CANCELLING || this == FAILING || this == STOPPING;
    }


    public boolean isLost(){
        return this == NONE;
    }
    /**
     * status is running
     *
     * @return status
     */
    public boolean isRunning() {
        return this == RUNNING || this == SUBMITTING || this == INITIALIZING;
    }

    public static List<String> runningArray() {
        ArrayList<String> integers = new ArrayList<>();
        integers.add(RUNNING.name());
        integers.add(SUBMITTING.name());
        integers.add(CANCELLING.name());
        integers.add(STOPPING.name());
        integers.add(INITIALIZING.name());
        integers.add(FAILING.name());
        return integers;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static ExecStatus of(int status) {
        if (EXECUTION_STATUS_MAP.containsKey(status)) {
            return EXECUTION_STATUS_MAP.get(status);
        }
        throw new IllegalArgumentException("invalid status : " + status);
    }

    public static ExecStatus of(String value){
        return Arrays.stream(ExecStatus.values()).filter(execStatus -> {
            return execStatus.name().equals(value);
        }).findFirst().orElse(null);
    }
}
