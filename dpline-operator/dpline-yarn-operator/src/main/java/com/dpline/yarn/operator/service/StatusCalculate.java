package com.dpline.yarn.operator.service;

import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.util.Asserts;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.operator.common.WatcherConfig;
import com.dpline.operator.entity.ApplicationEntity;
import com.dpline.operator.entity.ClusterEntity;
import com.dpline.operator.entity.JobTask;
import com.dpline.operator.entity.TaskFlushEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 状态引擎
 */
@Component
public class StatusCalculate {

    @Autowired
    WatcherConfig watcherConfig;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    JobMapper jobMapper;

    private Logger logger = LoggerFactory.getLogger(StatusCalculate.class);
    /**
     * 推断每个任务的最终状态
     *
     * @param jobId
     * @param newExecStatus
     * @return
     */
    private ExecStatus inferFinalStatus(JobTask jobTask, String jobId, ExecStatus newExecStatus) {
        if (Asserts.isNull(jobTask)) {
            logger.warn("Flink task jobId [{}] not find in cache.", jobId);
            return newExecStatus;
        }
        ExecStatus oldExecStatus = jobTask.getExecStatus();
        String taskName = jobTask.getTaskName();
        long taskId = jobTask.getJobId();
        logger.debug("ID:{},TaskName:[{}] status before:[{}], now: [{}],", taskId,
            taskName,
            oldExecStatus.name(), newExecStatus.name());
        // 现在的状态是None，并且之前也是None，大于了10s
        boolean terminated = isTerminated(jobTask, newExecStatus);
        if (terminated) {
            newExecStatus = ExecStatus.TERMINATED;
        }
        // 如果任务已经丢失，判断任务是否已经处于停止，状态
        boolean stopped = isStopped(oldExecStatus, newExecStatus, jobId);
        if (stopped) {
            newExecStatus = ExecStatus.STOPPED;
        }
        logger.info("ID:{},TaskName:[{}] status before:[{}], now: [{}],", taskId,
            taskName,
            oldExecStatus.name(),
            newExecStatus.name());
        return newExecStatus;
    }

    Map<String,ExecStatus> inferFinalStatus(ApplicationEntity applicationEntity, Map<String,ExecStatus> newExecStatusMap){
        applicationEntity.getJobTaskMap().forEach((jobId, jobTask) -> {
                ExecStatus execStatus = inferFinalStatus(jobTask,
                            jobTask.getRunJobId(),
                            newExecStatusMap.getOrDefault(jobTask.getRunJobId(), ExecStatus.NONE));
                newExecStatusMap.put(jobId, execStatus);
            }
        );
        return newExecStatusMap;
    }


    /**
     * case when always NONE status, and arrive at time，then become terminated
     *
     * @param jobTask
     * @param newExecStatus
     * @return
     */
    private boolean isTerminated(JobTask jobTask, ExecStatus newExecStatus) {
        if (newExecStatus.isLost() &&
                jobTask.getExecStatus() == newExecStatus &&
            System.currentTimeMillis() - jobTask.getCurrentTimeStamp() > (watcherConfig.getNoneStatusChangeMaxTime())) {
            return true;
        }
        return false;
    }

    private boolean isStopped(ExecStatus oldExecStatus, ExecStatus newExecStatus, String jobId) {
        OperationsEnum operationFromCache = taskClusterMapService.getOperationFromCache(jobId);
        if (oldExecStatus != newExecStatus && newExecStatus.isLost() && OperationsEnum.STOP.equals(operationFromCache)) {
            return true;
        }
        return false;
    }

}
