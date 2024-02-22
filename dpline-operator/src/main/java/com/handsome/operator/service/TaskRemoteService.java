package com.handsome.operator.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.handsome.common.enums.ExecStatus;
import com.handsome.common.util.StringUtils;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import com.handsome.operator.job.ClusterFlushEntity;
import com.handsome.operator.k8s.K8sClientManager;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static com.handsome.operator.job.TaskRestUrlStatusConvertion.REST_JOBS_OVERVIEWS;

@Service
public class TaskRemoteService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(TaskRemoteService.class);

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    K8sClientManager k8sClientManager;

    private static final int FLINK_CLIENT_TIMEOUT_SEC = 30000;

    /**
     * 1、需要获取到 rest-url,更新db的rest-url;
     * 2、获取到 k8s 的event信息;
     * 3、两者交互获取任务执行状态;
     *
     * @throws InterruptedException
     */
    public ExecStatus doRemoteToK8s(ClusterFlushEntity clusterInst) {
        ExecStatus execStatus = ExecStatus.NONE;
        try {
            boolean deploymentExists = k8sClientManager.isDeploymentExists(
                clusterInst.getK8sClientKey(),
                clusterInst.getClusterId(),
                clusterInst.getNameSpace());
            // no exist，如果
            if (!deploymentExists) {
                return inferTaskStatusFromCacheBefore(clusterInst);
            }

            Optional<ContainerStatus> podsStatus = k8sClientManager.getPodsStatus(k8sClientManager.getPods(clusterInst.getK8sClientKey(),
                clusterInst.getClusterId(), clusterInst.getNameSpace()));
            boolean podTerminatedBefore = k8sClientManager.isPodTerminatedBefore(podsStatus);
            boolean podRunningNow = k8sClientManager.isPodRunningNow(podsStatus);

            // not terminated,running
            if (podRunningNow) {
                // retrieve flink cluster client
//                String deploymentRestUrl = TaskPath.getRestUrlPath(clusterInst.getClusterId());
//                clusterInst.setRestUrl(deploymentRestUrl);
//                //  直接更新rest-url,session模式，需要根据 session id，去批量更新
//                if(clusterInst.getRunModeType().equals(RunModeType.K8S_APPLICATION)){
//                    ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterInst.getTaskFlushEntityMap().get(clusterInst.getClusterId());
//                    flinkTaskInstanceMapper.updateRestUrl(taskFlushEntity.getTaskId(),deploymentRestUrl);
//                } else {
//                    // 更新session模式下的所有的 task 的url
//                    ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterInst.getTaskFlushEntityMap().get(clusterInst.getClusterId());
//                    flinkTaskInstanceMapper.batchUpdateSessionRestUrl(taskFlushEntity.getTaskId(),deploymentRestUrl);
                logger.warn("The task [{}] is running on k8s.please check your rest-url.",clusterInst.getClusterId());

//                Map<String, ClusterFlushEntity.TaskFlushEntity> taskFlushEntityMap = clusterInst.getTaskFlushEntityMap();
//                taskFlushEntityMap.forEach();
                return ExecStatus.RUNNING;
            }

            // Terminated Before
            if (podTerminatedBefore) {
                // jobManager down, write log logger.error("");
                String logPath = k8sClientManager.writeDeploymentLog(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
                // delete deployment,
                k8sClientManager.deleteDeployment(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
                k8sClientManager.deleteConfigMaps(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
//                k8sClientManager.deleteDeployment(clusterInst.getK8sClientKey(), clusterInst.getNameSpace(), clusterInst.getClusterId());
                logger.warn("The task is failed before.Delete Deployment and write log to [{}]",logPath);
                return ExecStatus.FAILED;
            }
            return ExecStatus.INITIALIZING;
        } catch (Exception e){
            e.printStackTrace();
        }
        return execStatus;
    }

    /**
     * 1.如果之前的状态是 running, 然后没有查到部署的话，为 none
     * 2.如果之前是正在停止的话，需要设置为停止状态
     */
    private ExecStatus inferTaskStatusFromCacheBefore(ClusterFlushEntity clusterInst) {
        ClusterFlushEntity.TaskFlushEntity taskFlushEntity = clusterInst.getTaskFlushEntityMap().get(clusterInst.getClusterId());
        ExecStatus execStatus = taskFlushEntity.getExecStatus();
        if (execStatus.equals(ExecStatus.FAILING)) {
            return ExecStatus.FAILED;
        }
        if (execStatus.equals(ExecStatus.CANCELLING)) {
            return ExecStatus.CANCELED;
        }
        return ExecStatus.NONE;
    }

    /**
     * 请求rest接口，获取状态
     *
     * @param clusterFlushEntity
     */
    public Optional<String> doRequestToRestUrl(ClusterFlushEntity clusterFlushEntity) {
        try {
            if (StringUtils.isEmpty(clusterFlushEntity.getRestUrl())) {
                logger.error("Rest url is empty.");
                return Optional.empty();
            }
            HttpResponse response = HttpRequest.get(String.format(REST_JOBS_OVERVIEWS,clusterFlushEntity.getRestUrl()))
                .timeout(FLINK_CLIENT_TIMEOUT_SEC)
                .charset(StandardCharsets.UTF_8)
                .contentType("application/json").execute();
            // 如果不可达的话，需要看看不可达的时间戳，如果高于10s的话，就判断为失败
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                logger.warn("Rest-url [{}] is not remote for status [{}]", clusterFlushEntity.getRestUrl(), response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.body());
        } catch (Exception e) {
            logger.warn("Rest-url [{}] is not remote for error [{}]", clusterFlushEntity.getRestUrl(), e.toString());
        }
        return Optional.empty();
    }
}
