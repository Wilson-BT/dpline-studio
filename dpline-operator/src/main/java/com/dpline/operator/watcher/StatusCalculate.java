package com.dpline.operator.watcher;

import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.util.Asserts;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.operator.config.WatcherConfig;
import com.dpline.operator.job.ClusterFlushEntity;
import com.dpline.operator.job.TaskFlushEntity;
import com.dpline.operator.service.TaskClusterMapService;
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
    private ExecStatus inferFinalStatus(TaskFlushEntity taskFlushEntity, String jobId, ExecStatus newExecStatus) {
        if (Asserts.isNull(taskFlushEntity)) {
            logger.warn("Flink task jobId [{}] not find in cache.", jobId);
            return newExecStatus;
        }
        ExecStatus oldExecStatus = taskFlushEntity.getExecStatus();
        String taskName = taskFlushEntity.getTaskName();
        long taskId = taskFlushEntity.getJobId();
        logger.debug("ID:{},TaskName:[{}] status before:[{}], now: [{}],", taskId,
            taskName,
            oldExecStatus.name(), newExecStatus.name());
        // 现在的状态是None，并且之前也是None，大于了10s
        boolean terminated = isTerminated(taskFlushEntity, newExecStatus);
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

    Map<String,ExecStatus> inferFinalStatus(ClusterFlushEntity clusterFlushEntity, Map<String,ExecStatus> newExecStatusMap){
        clusterFlushEntity.getTaskFlushEntityMap().forEach((jobId,taskFlushEntity) -> {
                ExecStatus execStatus = inferFinalStatus(taskFlushEntity,
                            taskFlushEntity.getRunJobId(),
                            newExecStatusMap.getOrDefault(taskFlushEntity.getRunJobId(), ExecStatus.NONE));
                newExecStatusMap.put(jobId, execStatus);

            }
        );
        return newExecStatusMap;
    }


    /**
     * case when always NONE status, and arrive at time，then become terminated
     *
     * @param taskFlushEntity
     * @param newExecStatus
     * @return
     */
    private boolean isTerminated(TaskFlushEntity taskFlushEntity, ExecStatus newExecStatus) {
        if (newExecStatus.isLost() &&
            taskFlushEntity.getExecStatus() == newExecStatus &&
            System.currentTimeMillis() - taskFlushEntity.getCurrentTimeStamp() > (watcherConfig.getNoneStatusChangeMaxTime())) {
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
