package com.handsome.operator.service;

import com.handsome.common.K8sClientManager;
import com.handsome.common.enums.ExecStatus;
import com.handsome.common.enums.OperationsEnum;
import com.handsome.common.enums.ResponseStatus;
import com.handsome.common.request.*;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.CodeGenerateUtils;
import com.handsome.dao.entity.FlinkRunTaskInstance;
import com.handsome.dao.mapper.FlinkTaskInstanceMapper;
import com.handsome.dao.mapper.TaskSavepointMapper;
import com.handsome.flink.api.FlinkTaskOperateProxy;
import com.handsome.operator.job.ClusterFlushEntity;
import com.handsome.operator.job.TaskStatusWatchManager;
import com.handsome.operator.k8s.PodYamlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class TaskOperateService extends BaseService {

    private final static Logger logger = LoggerFactory.getLogger(TaskOperateService.class);

    @Autowired
    FlinkTaskInstanceMapper flinkTaskInstanceMapper;

    @Autowired
    TaskStatusWatchManager statusChangeManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    TaskStatusService taskStatusService;

    @Autowired
    TaskSavepointMapper taskSavepointMapper;

    FlinkTaskOperateProxy flinkTaskOperateProxy = new FlinkTaskOperateProxy();


    /**
     * task submit and add to watcher
     * if application mode， need create pod file，session mode，no need
     *
     * @param submitRequest
     */
    public SubmitResponse submitAndWatch(SubmitRequest submitRequest) {
        SubmitResponse submitResponse = new SubmitResponse();
        try {
            // 生成 pod 文件
            String podFilePath = createTemPodFile(submitRequest);
            submitRequest.getK8sOptions().setPodFilePath(podFilePath);
            submitResponse = flinkTaskOperateProxy.submit(submitRequest);
            if (Asserts.isNotNull(submitResponse)
                && submitResponse.getResponseStatus().equals(ResponseStatus.SUCCESS)
            ) {
                addToWatcher(submitResponse);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            submitResponse.setResponseStatus(ResponseStatus.FAIL);
        }
        return submitResponse;
    }


    /**
     * stop online task
     *
     * @param stopRequest
     */
    public StopResponse stop(StopRequest stopRequest) throws Exception {
        // un stop 的人 stop 掉之后，是否需要减去
        taskClusterMapService.addOperationToCache(stopRequest.getJobId(), OperationsEnum.STOP);
        StopResponse stopResponse = flinkTaskOperateProxy.stop(stopRequest);
        if(stopResponse.getResponseStatus().equals(ResponseStatus.FAIL)){
            taskClusterMapService.delOperationFromCache(stopRequest.getJobId());
        }
        return stopResponse;
    }

    private boolean addToWatcher(SubmitResponse submitResponse) {
        FlinkRunTaskInstance flinkRunTaskInstance = flinkTaskInstanceMapper.selectApplicationInfoById(submitResponse.getTaskId());
        flinkRunTaskInstance.setRestUrl(submitResponse.getRestUrl());
        flinkTaskInstanceMapper.updateRestUrl(
            flinkRunTaskInstance.getId(),
            submitResponse.getJobId(),
            submitResponse.getRestUrl());
        // 更新任务
        return statusChangeManager.submitToUpdateCache(flinkRunTaskInstance);
    }


    /**
     * create pod file,if session mode,No pod file
     *
     * @return
     */
    @SuppressWarnings("all")
    private String createTemPodFile(SubmitRequest submitRequest) throws IOException {
        switch (submitRequest.getOtherOptions().getRunModeType()) {
            case LOCAL:
                break;
            case K8S_APPLICATION:
                return new PodYamlManager(submitRequest)
                        .initPodTemplate();
            default:
                throw new IllegalArgumentException(String.format("UnSupport run mode type for [{}]", submitRequest.getOtherOptions().getRunModeType()));
        }
        return "";
    }

    /**
     * trigger online task
     */
    public TriggerResponse trigger(TriggerRequest triggerRequest) throws IOException, URISyntaxException, CodeGenerateUtils.CodeGenerateException {
        // un stop 的人 stop 掉之后，是否需要减去
        return flinkTaskOperateProxy.trigger(triggerRequest);
    }


}
