package com.dpline.operator.watcher;

import com.dpline.common.enums.ClusterType;
import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.util.Asserts;
import com.dpline.operator.job.ClusterFlushEntity;
import com.dpline.operator.k8s.K8sClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskStatusRemoteProxy implements StatusTrack {

    @Autowired
    StatusCalculate statusCalculate;

    @Autowired
    K8sClusterManager k8sClusterManager;


    private ConcurrentHashMap<String, StatusTrack> runModeStatusTrackMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(TaskStatusRemoteProxy.class);

    // 对每个任务进行

    /**
     * first use rest url
     * then application mode use k8s deployment
     *      session mode only use rest-url
     *
     * @param clusterFlushEntity
     * @return
     */
    @Override
    public Map<String, ExecStatus> remote(ClusterFlushEntity clusterFlushEntity) {
        Optional<StatusTrack> statusTrackOptional = getStatusTrack(clusterFlushEntity.getRunModeType(), RemoteType.REST);
        return statusTrackOptional.map(
            resStatusTrack -> {
                Map<String, ExecStatus> remoteExecStatus = resStatusTrack.remote(clusterFlushEntity);
                // session mode only judge by rest url
                // if k8s application and none，need
                if ((Asserts.isNull(remoteExecStatus) || remoteExecStatus.isEmpty()) &&
                    clusterFlushEntity.getRunModeType().getClusterType().equals(ClusterType.KUBERNETES)) {
                    logger.info("Rest url get status failed, ready remote to k8s deployment.");
                    remoteExecStatus = getStatusTrack(clusterFlushEntity.getRunModeType(), RemoteType.K8S)
                        .map(k8sStatusTrack -> {
                            return k8sStatusTrack.remote(clusterFlushEntity);
                        }).orElse(new HashMap<>());
                }
                return statusCalculate.inferFinalStatus(clusterFlushEntity, remoteExecStatus);
            }).orElse(new HashMap<>());
    }

    public Optional<StatusTrack> getStatusTrack(RunModeType runModeType, RemoteType remoteType) {
        return Optional.ofNullable(getOrCreateStatusTrack(runModeType, remoteType));
    }

    /**
     * TODO 需要优化为反射 ServiceLoad 加载
     *
     * @param runModeType
     * @param remoteType
     * @return
     */
    private StatusTrack getOrCreateStatusTrack(RunModeType runModeType, RemoteType remoteType) {
        String key = runModeType.getValue() + "_" + remoteType.key;
        if (runModeStatusTrackMap.containsKey(key)) {
            return runModeStatusTrackMap.get(key);
        }
        if (RemoteType.REST.equals(remoteType)) {
            runModeStatusTrackMap.putIfAbsent(key, new RestStatusRemoteTrack());
        }
        if (RunModeType.K8S_APPLICATION.equals(runModeType) && RemoteType.K8S.equals(remoteType)) {
            runModeStatusTrackMap.putIfAbsent(key,
                new K8sApplicationStatusRemoteTrack(k8sClusterManager));
        }
        if (RunModeType.K8S_SESSION.equals(runModeType) && RemoteType.K8S.equals(remoteType)) {
            runModeStatusTrackMap.putIfAbsent(key,
                new K8sSessionStatusRemoteTrack(k8sClusterManager));
        }
        return runModeStatusTrackMap.get(key);
    }


    public enum RemoteType {
        REST(0, "rest"),
        K8S(1, "k8s");
        private int key;
        private String type;

        RemoteType(int key, String type) {
            this.key = key;
            this.type = type;
        }

        public Optional<RemoteType> of(String type) {
            for (RemoteType remoteType : RemoteType.values()) {
                if (remoteType.type.equals(type)) {
                    return Optional.of(remoteType);
                }
            }
            return Optional.empty();
        }

    }


}
