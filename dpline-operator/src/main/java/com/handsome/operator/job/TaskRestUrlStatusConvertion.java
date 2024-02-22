package com.handsome.operator.job;

import com.handsome.common.enums.ExecStatus;

import java.util.Locale;
import java.util.Optional;

/**
 *  任务运行的rest接口状态
 */
public class TaskRestUrlStatusConvertion {


    public static String APPLICATION_MODE_JOB_ID = "00000000000000000000000000000000";

    public static String REST_JOBS_OVERVIEWS ="%s/jobs/overview";

    public static String REST_JOB_STATUS="%s/jobs";


    public static ExecStatus restStatusConvertToExec(RestRunStatus taskRunStatus){
        switch (taskRunStatus){
            // 如果是初始化或者创建完成，
            case INITIALIZING:
            case CREATED:
                return ExecStatus.INITIALIZING;
            case RUNNING:
                return ExecStatus.RUNNING;
            case FAILING:
            case RESTARTING:
            case SUSPENDED:
            case RECONCILING:
                return ExecStatus.FAILING;
            case FAILED:
                return ExecStatus.FAILED;
            case CANCELLING:
                return ExecStatus.CANCELLING;
            case CANCELED:
                return ExecStatus.CANCELED;
            case FINISHED:
                return ExecStatus.FINISHED;
        }
        return ExecStatus.NONE;
    }
    public enum RestRunStatus {
        INITIALIZING, CREATED, RUNNING, FAILING, FAILED, CANCELLING, CANCELED, FINISHED, RESTARTING, SUSPENDED, RECONCILING;
        public static Optional<RestRunStatus> of(String restStatus){
            for (RestRunStatus rest: RestRunStatus.values()) {
                if (rest.name().equals(restStatus.toUpperCase(Locale.ROOT))){
                    return Optional.of(rest);
                }
            }
            return Optional.empty();
        }
    }
}
