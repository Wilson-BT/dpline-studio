package com.handsome.operator.service;

import com.handsome.alert.api.AlertEntity;
import com.handsome.alert.api.AlertManager;
import com.handsome.common.enums.AlertType;
import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.OperationsEnum;
import com.handsome.common.enums.RunModeType;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import com.handsome.operator.config.WatcherConfig;
import com.handsome.operator.job.ClusterFlushEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;

@Service
public class TaskStatusService extends BaseService {

    private static Logger logger = LoggerFactory.getLogger(TaskStatusService.class);

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    WatcherConfig watcherConfig;

    @Autowired
    AlertManager alterManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    LinkedBlockingDeque<ClusterFlushEntity> blockingQueue;

    /**
     * Update db and cache because status changed
     * submit/init -> running
     * submit/init/running -> failing
     * running -> cancelling
     * cancelling -> cancelled
     * failing -> failed
     * submit/init/running -> none
     * none -> none && 10s => terminated
     * <p>
     * 有可能手动停止和自动探寻冲突，需要加锁
     *
     * @param
     */
    public boolean updateTaskInstStatus(ClusterFlushEntity clusterFlushEntity, String jobId, ExecStatus newExecStatus) {
        ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterFlushEntity.getTaskFlushEntityMap().get(jobId);
        if (taskFlushEntity == null) {
            logger.warn("Flink task jobId [{}] not find in cache.", jobId);
            return false;
        }
        ExecStatus oldExecStatus = taskFlushEntity.getExecStatus();
        String taskName = taskFlushEntity.getTaskName();
        long taskId = taskFlushEntity.getTaskId();
        logger.debug("ID:{},TaskName:[{}] status before:[{}], now: [{}],", taskId,
            taskName,
            oldExecStatus.name(), newExecStatus.name());
        long currentTimeMillis = System.currentTimeMillis();
        // 现在的状态是None，并且之前也是None，大于了10s
        boolean terminated = isTerminated(taskFlushEntity, newExecStatus, currentTimeMillis);
        if (terminated) {
            newExecStatus = ExecStatus.TERMINATED;
        }
        boolean stopped = isStop(oldExecStatus, newExecStatus, jobId);
        if (stopped) {
            newExecStatus = ExecStatus.STOPPED;
        }
        // if status is same, and is none, do nothing, else update timestamp
        if (oldExecStatus != newExecStatus || !newExecStatus.isLost()) {
            taskFlushEntity.setCurrentTimeStamp(currentTimeMillis);
        }
        // update database
        updateDB(taskFlushEntity, newExecStatus);
        logger.info("ID:{},TaskName:[{}] status before:[{}], now: [{}],", taskId,
            taskName,
            oldExecStatus.name(), newExecStatus.name());
        // if status is not same,update database
        if (newExecStatus.isStopped()) {
            taskClusterMapService.delOperationFromCache(jobId);
            delTaskInstFromCache(clusterFlushEntity, jobId);
            return true;
        }
        taskClusterMapService.addOrUpdateClusterMap(clusterFlushEntity);
        return true;
    }

    private void updateDB(ClusterFlushEntity.TaskFlushEntity taskFlushEntity, ExecStatus newExecStatus) {
        if (newExecStatus.equals(taskFlushEntity.getExecStatus())) {
            return;
        }
        try {
            flinkTaskInstanceMapper.updateExecStatus(taskFlushEntity.getTaskId(), newExecStatus.getCode());
            callAlert(taskFlushEntity.getAlertType(),
                taskFlushEntity.getAlertInstanceId(),
                taskFlushEntity.getTaskName(),
                taskFlushEntity.getExecStatus().name(),
                newExecStatus.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        taskFlushEntity.setExecStatus(newExecStatus);
    }

    /**
     * 是否被 Cancelled 了
     *
     * @param oldExecStatus
     * @param newExecStatus
     * @param jobId
     * @return
     */
    private boolean isCancelled(ExecStatus oldExecStatus, ExecStatus newExecStatus, String jobId) {
        OperationsEnum operationFromCache = taskClusterMapService.getOperationFromCache(jobId);
        if (oldExecStatus != newExecStatus && newExecStatus.isLost() && OperationsEnum.CANCEL.equals(operationFromCache)) {
            return true;
        }
        if (oldExecStatus != newExecStatus && newExecStatus.equals(ExecStatus.CANCELED)){
            return true;
        }
        return false;
    }

    /**
     * 是否被停止了
     *
     * @param oldExecStatus
     * @param newExecStatus
     * @param jobId
     * @return
     */
    private boolean isStop(ExecStatus oldExecStatus, ExecStatus newExecStatus, String jobId) {
        OperationsEnum operationFromCache = taskClusterMapService.getOperationFromCache(jobId);
        if (oldExecStatus != newExecStatus && newExecStatus.isLost() && OperationsEnum.STOP.equals(operationFromCache)) {
            return true;
        }
        if (oldExecStatus != newExecStatus && newExecStatus.equals(ExecStatus.STOPPED)){
            return true;
        }
        return false;
    }

    /**
     * 是否被后台中断了
     * None 大于 10s 则判为 TERMINATED
     *
     * @return
     */
    private boolean isTerminated(ClusterFlushEntity.TaskFlushEntity taskFlushEntity, ExecStatus newExecStatus, long currentTimeMillis) {
        if (newExecStatus.isLost() &&
            taskFlushEntity.getExecStatus() == newExecStatus &&
            currentTimeMillis - taskFlushEntity.getCurrentTimeStamp() > (watcherConfig.getNoneStatusChangeMaxTime())) {
            return true;
        }
        return false;
    }

    /**
     * 任务统一删除入口
     * application 模式： 直接删除任务，并删除ingress
     * session 模式：删除cluster中的相关任务，但是并不删除 Session
     *
     * @param clusterFlushEntity
     * @param jobId
     */
    public void delTaskInstFromCache(ClusterFlushEntity clusterFlushEntity, String jobId) {
        if (clusterFlushEntity.getRunModeType().equals(RunModeType.K8S_APPLICATION)) {
            taskClusterMapService.delApplicationInstFromClusterMap(clusterFlushEntity.getK8sClientKey(), clusterFlushEntity.getClusterId());
        }
        taskClusterMapService.delSessionInstFromClusterMap(clusterFlushEntity.getK8sClientKey(), clusterFlushEntity.getClusterId(), jobId);
    }

    /**
     * call alertService to send alert
     *
     * @param taskName
     */
    public void callAlert(AlertType alertType, int alertInstanceId, String taskName, String taskStatusBefore, String taskStatusCurrent) {
        if (!alertType.equals(AlertType.NONE)) {
            alterManager.init().accept(new AlertEntity(alertType, alertInstanceId, taskName, taskStatusBefore, taskStatusCurrent));
        }
    }

}
