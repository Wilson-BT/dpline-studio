package com.dpline.k8s.operator.service;

import com.dpline.common.enums.*;
import com.dpline.common.request.*;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.dao.entity.Job;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.flink.api.TaskOperateProxy;
import com.dpline.k8s.operator.watcher.TaskStatusManager;
import com.dpline.k8s.operator.k8s.PodYamlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskOperateService extends BaseService {

    private final static Logger logger = LoggerFactory.getLogger(TaskOperateService.class);

    @Autowired
    JobMapper jobMapper;

    @Autowired
    TaskStatusManager taskStatusManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    /**
     * job submit and add to watcher
     * if application mode， need create pod file，session mode，no need
     *
     * @param submitRequest
     */
    public SubmitResponse submitAndWatch(FlinkK8sRemoteSubmitRequest submitRequest) {
        SubmitResponse submitResponse = new SubmitResponse();
        try {
            // k8s application mode create pod file
            createPodFile(submitRequest);
            // delete configmap
            submitResponse = (SubmitResponse) TaskOperateProxy.execute(OperationsEnum.START,
                submitRequest);
            // 成功则加入监控
            logger.info("SubmitResponse: {}",submitResponse.toString());
            if (submitResponse.getResponseStatus().equals(ResponseStatus.SUCCESS)
                && addToWatcher(submitResponse)) {
                return submitResponse;
            }
            submitResponse.setResponseStatus(ResponseStatus.FAIL);
        } catch (Exception exception) {
            submitResponse.setResponseStatus(ResponseStatus.FAIL);
            submitResponse.setMsg(ExceptionUtil.exceptionToString(exception));
        }
        return submitResponse;
    }

    /**
     * stop online task
     *
     * @param stopRequest
     */
    public StopResponse stop(FlinkStopRequest stopRequest) throws Exception {
        // un stop 的人 stop 掉之后，是否需要减去
        taskClusterMapService.addOperationToCache(stopRequest.getRunJobId(), OperationsEnum.STOP);
        // 更新为 正在停止状态
        StopResponse stopResponse = new StopResponse(
            ResponseStatus.FAIL,
            "");
        try {
            stopResponse =(StopResponse) TaskOperateProxy.execute(
                OperationsEnum.STOP,
                stopRequest);
        } catch (Exception e) {
            logger.error("Request to stop failed,", e);
        } finally {
            // 只有失败的时候，才删除，如果成功，则由后台自动删除
            if(stopResponse.getResponseStatus().equals(ResponseStatus.FAIL)){
                taskClusterMapService.delOperationFromCache(stopRequest.getRunJobId());
            }
        }
        return stopResponse;
    }

    /**
     * 更新数据库状态、添加监控
     *
     * @param submitResponse
     * @return
     */
    private boolean addToWatcher(SubmitResponse submitResponse) {
        Job job = jobMapper.selectById(submitResponse.getJobId());
        // 将任务缓存起来
        return taskStatusManager.submitToMonitor(job);
    }

    /**
     * create pod file,if session mode,No pod file
     *
     * @return
     */
    private void createPodFile(FlinkK8sRemoteSubmitRequest submitRequest) throws IOException {
        if(Asserts.isNull(submitRequest)){
            return;
        }
        switch (submitRequest.getRunModeType()) {
            case K8S_APPLICATION:
                // 设置 pod file 和 jar 运行位置
                String podPath = PodYamlUtil.initPodTemplate(submitRequest);
                logger.info("podPath: {}",podPath);
                submitRequest.getK8sOptions()
                    .setPodFilePath(podPath);
                break;
            default:
                throw new IllegalArgumentException(String.format("UnSupport run mode type for %s", submitRequest.getRunModeType()));
        }
    }

    /**
     * trigger online task
     */
    public TriggerResponse trigger(FlinkTriggerRequest triggerRequest) throws Exception {
        // un stop 的人 stop 掉之后，是否需要减去
        return (TriggerResponse) TaskOperateProxy.execute(OperationsEnum.TRIGGER,triggerRequest);
    }

}
