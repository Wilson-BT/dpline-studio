package com.dpline.yarn.operator.service;

import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.*;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.dao.entity.Job;
import com.dpline.dao.mapper.JobMapper;
import com.dpline.flink.api.TaskOperateProxy;
import com.dpline.yarn.operator.watcher.TaskStatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskOperatorService {

    @Autowired
    TaskStatusManager taskStatusManager;

    @Autowired
    TaskClusterMapService taskClusterMapService;

    @Autowired
    JobMapper jobMapper;

    private Logger logger = LoggerFactory.getLogger(TaskOperatorService.class);

    /**
     * submit
     * @param
     */
    public SubmitResponse submitAndWatch(YarnRemoteSubmitRequest flinkSubmitRequest){
        SubmitResponse submitResponse = new SubmitResponse();
        try {
            submitResponse = (SubmitResponse) TaskOperateProxy.execute(OperationsEnum.START,
                    flinkSubmitRequest);
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

    private boolean addToWatcher(SubmitResponse submitResponse) {
        Job job = jobMapper.selectById(submitResponse.getJobId());
        // update rest-url
        job.setRestUrl(submitResponse.getRestUrl());
        // 将任务缓存起来
        return taskStatusManager.submitToMonitor(job);
    }

    /**
     * stop
     *
     * @param stopRequest
     * @return
     * @throws Exception
     */
    public StopResponse stop(FlinkStopRequest stopRequest) throws Exception{
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
     * trigger
     * @param triggerRequest
     * @return
     * @throws Exception
     */
    public TriggerResponse trigger(FlinkTriggerRequest triggerRequest) throws Exception{
        return (TriggerResponse) TaskOperateProxy.execute(OperationsEnum.TRIGGER,triggerRequest);
    }

}
