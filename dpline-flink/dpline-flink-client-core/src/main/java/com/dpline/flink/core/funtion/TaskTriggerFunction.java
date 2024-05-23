package com.dpline.flink.core.funtion;

import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.FlinkTriggerRequest;
import com.dpline.common.request.Response;
import com.dpline.common.request.TriggerResponse;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import com.dpline.flink.core.TaskOperator;
import com.dpline.flink.core.util.FlinkRestUtil;

import static com.dpline.common.util.HttpUtils.logger;

public class TaskTriggerFunction extends TaskOperator {

    @Override
    public Response apply(FlinkRequest request) {
        FlinkTriggerRequest triggerRequest = (FlinkTriggerRequest)request;
        TriggerResponse triggerResponse = new TriggerResponse(
            triggerRequest.getJobId(),
            triggerRequest.getSavePointAddress(),
            ResponseStatus.FAIL
        );
        try {
            String savePointPath = FlinkRestUtil.getInstance().cancelOrSavePoint(
                triggerRequest.getRunJobId(),
//                TaskPathResolver.getNewRestUrlPath(triggerRequest.getNameSpace(),
//                        triggerRequest.getIngressHost(),
//                        triggerRequest.getClusterId()),
                triggerRequest.getRestUrl(),
                triggerRequest.getSavePointAddress(),
                false);
            if(StringUtils.isNotEmpty(savePointPath)){
                logger.info("Cluster:[{}], Task: [{}] saved to Path [{}] success.",
                    triggerRequest.getClusterId(),
                    triggerRequest.getRunJobId(),
                    savePointPath);
                triggerResponse.setSavePointAddress(savePointPath);
                triggerResponse.setResponseStatus(ResponseStatus.SUCCESS);
                return triggerResponse;
            }
            logger.error("Cluster:[{}], Task: [{}] saved to Path [{}] failed",
                triggerRequest.getClusterId(),
                triggerRequest.getRunJobId(),
                triggerRequest.getSavePointAddress()
            );
        } catch (Exception ex) {
            triggerResponse.setMsg(ExceptionUtil.exceptionToString(ex));
            logger.error("TriggerSavePoint failed,", ex);
        }
        return triggerResponse;
    }

}
