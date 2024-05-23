package com.dpline.operator.common;

import com.dpline.common.enums.ExecStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Optional;

/**
 *  任务运行的rest接口状态
 */
public class TaskRestUrlStatusConvertor {


    public static String REST_JOBS_OVERVIEWS ="%s/jobs/overview";

    private static Logger logger = LoggerFactory.getLogger(TaskRestUrlStatusConvertor.class);

    /**
     * Arguments.of(INITIALIZING, true, false),
     *                 Arguments.of(CREATED, true, false),
     *                 Arguments.of(RUNNING, false, true),
     *                 Arguments.of(FAILING, true, false),
     *                 Arguments.of(FAILED, false, false),
     *                 Arguments.of(CANCELLING, true, false),
     *                 Arguments.of(CANCELED, false, false),
     *                 Arguments.of(FINISHED, false, false),
     *                 Arguments.of(RESTARTING, true, false),
     *                 Arguments.of(SUSPENDED, true, false),
     *                 Arguments.of(RECONCILING, true, false));
     * @param taskRunStatus
     * @return
     */
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
        logger.warn("Rest-url status is [{}]", taskRunStatus.name());
        return ExecStatus.NONE;
    }
    public static ExecStatus yarnStatusConvertToLocal(String state){
        YarnAppState yarnAppState = YarnAppState.of(state);
        logger.info("Yarn application state is {}.", yarnAppState.name());
        switch (yarnAppState){
            // 如果是初始化或者创建完成，
            case NEW:
            case NEW_SAVING:
            case ACCEPTED:
            case SUBMITTED:
                return ExecStatus.INITIALIZING;
            case RUNNING:
                return ExecStatus.RUNNING;
            case FAILED:
                return ExecStatus.FAILED;
            case KILLED:
                return ExecStatus.CANCELED;
            case FINISHED:
                return ExecStatus.FINISHED;
            case OTHER:
                return ExecStatus.NONE;
        }
        logger.warn("yarn-url status is [{}]", yarnAppState.name());
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

    public enum YarnAppState {
        NEW,
        NEW_SAVING,
        SUBMITTED,
        ACCEPTED,
        RUNNING,
        // FINAL_SAVING,
        // FINISHING,
        FINISHED,
        FAILED,
        // KILLING,
        KILLED,
        OTHER;
        public static YarnAppState of(String name) {
            for (YarnAppState yarnAppState : YarnAppState.values()) {
                if (yarnAppState.name().equals(name)) {
                    return yarnAppState;
                }
            }
            return null;
        }
    }


}
