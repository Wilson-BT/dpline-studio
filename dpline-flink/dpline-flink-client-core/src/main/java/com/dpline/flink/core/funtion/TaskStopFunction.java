package com.dpline.flink.core.funtion;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.*;
import com.dpline.common.util.*;
import com.dpline.flink.core.TaskOperator;
import com.dpline.flink.core.util.FlinkRestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaskStopFunction extends TaskOperator {

    private final static Logger logger = LoggerFactory.getLogger(TaskStopFunction.class);

    @Override
    public Response apply(FlinkRequest request) {
        StopResponse stopResponse = new StopResponse(
            ResponseStatus.FAIL,
            "");
        FlinkStopRequest stopRequest = (FlinkStopRequest) request;
        Boolean success;
        String runJobId = stopRequest.getRunJobId();
//        String restUrlPath = TaskPathResolver.getNewRestUrlPath(stopRequest.getRestUrl());
        try {
            if (stopRequest.getWithSavePointAddress()){
                String savePointAddress = null;
                savePointAddress = FlinkRestUtil.getInstance()
                    .cancelOrSavePoint(runJobId,
                        stopRequest.getRestUrl(),
                        stopRequest.getSavePointAddress(),
                        true);
                success = StringUtils.isNotBlank(savePointAddress);
            } else {
                success = FlinkRestUtil.getInstance().cancel(runJobId, stopRequest.getRestUrl());
            }
            if(success){
                logger.info("Cluster:[{}], Task: [{}] has been stopped success.",
                    stopRequest.getClusterId(),
                    stopRequest.getJobId());
                stopResponse.setResponseStatus(ResponseStatus.SUCCESS);
                stopResponse.setSavePointAddress(stopRequest.getSavePointAddress());
            } else {
                logger.error("Cluster:[{}], Task: [{}] has been stopped failed.",
                    stopRequest.getClusterId(),
                    stopRequest.getJobId()
                );
            }
        } catch (Exception ex) {
            stopResponse.setMsg(ExceptionUtil.exceptionToString(ex));
            logger.error(ExceptionUtil.exceptionToString(ex));
        }
        return stopResponse;
    }

}
