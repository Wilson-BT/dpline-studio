package com.dpline.yarn.operator.service;

import com.dpline.common.enums.ClusterType;
import com.dpline.common.enums.ExecStatus;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.JSONUtils;
import com.dpline.operator.common.RemoteType;
import com.dpline.operator.common.TaskRestUrlStatusConvertor;
import com.dpline.operator.entity.ApplicationEntity;
import com.dpline.operator.entity.ClusterEntity;
import com.dpline.operator.entity.JobTask;
import com.dpline.operator.entity.SniffEvent;
import com.dpline.operator.processor.StatusTrack;
import com.dpline.yarn.operator.Hadoop;
import com.dpline.yarn.operator.HadoopManager;
import com.dpline.yarn.operator.metric.YarnAppInfo;
import com.dpline.yarn.operator.remote.ApplicationStatusRemoteTrack;
import com.dpline.yarn.operator.remote.RestStatusRemoteTrack;
import com.dpline.yarn.operator.util.YarnUtil;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskStatusRemoteService implements StatusTrack {


    @Autowired
    HadoopManager hadoopManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    StatusCalculate statusCalculate;

    RestStatusRemoteTrack restStatusRemoteTrack = new RestStatusRemoteTrack();

    ApplicationStatusRemoteTrack applicationStatusRemoteTrack = new ApplicationStatusRemoteTrack();


    private ConcurrentHashMap<String, StatusTrack> runModeStatusTrackMap = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(TaskStatusRemoteService.class);



    /**
     * sniff event to remote
     *
     * @param sniffEvent
     * @return
     */
    public Map<String, ExecStatus> remote(SniffEvent sniffEvent) {
        Optional<ApplicationEntity> applicationEntityOptional = taskClusterMapService.getApplicationEntity(sniffEvent.getClusterId(), sniffEvent.getApplicationId());
        Optional<ClusterEntity> clusterEntity = taskClusterMapService.getClusterEntity(sniffEvent.getClusterId());
        if(!applicationEntityOptional.isPresent()){
            return new HashMap<>();
        }
        if(!clusterEntity.isPresent()){
            return new HashMap<>();
        }
        // TODO rest url remote
        Map<String, ExecStatus> remoteExecStatus = restRemote("");
        logger.info("Rest url get status failed, ready remote to yarn api.");
        // yarn java-api get
        if ((Asserts.isNull(remoteExecStatus) || remoteExecStatus.isEmpty())) {
            ExecStatus execStatus = applicationRemote(sniffEvent);
            // convert application to job
            remoteExecStatus = convertAppMapToJob(applicationEntityOptional.get(), execStatus);
        }

        return statusCalculate.inferFinalStatus(applicationEntityOptional.get(), remoteExecStatus);
    }

    /**
     * applicationId 的Map convert to jobId key
     * @param applicationEntity
     * @param remoteExecStatus
     */
    private Map<String, ExecStatus> convertAppMapToJob(ApplicationEntity applicationEntity, ExecStatus remoteExecStatus) {
        Map<String, ExecStatus> execStatusMap = new HashMap<>();
        applicationEntity.getJobTaskMap().forEach((jobId, jobTask) -> {
            execStatusMap.put(jobId, remoteExecStatus);
        });
        return execStatusMap;
    }

    @Override
    public Map<String, ExecStatus> restRemote(String url) {
        return restStatusRemoteTrack.restRemote(url);
    }

    /**
     * @param sniffEvent
     * @return
     */
    @Override
    public ExecStatus applicationRemote(SniffEvent sniffEvent) {
        return applicationStatusRemoteTrack.remote(sniffEvent);
    }
}
