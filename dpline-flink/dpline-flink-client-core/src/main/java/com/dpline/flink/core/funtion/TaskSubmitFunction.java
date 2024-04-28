package com.dpline.flink.core.funtion;

import com.dpline.common.request.*;
import com.dpline.common.util.Asserts;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.flink.core.TaskOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TaskSubmitFunction extends TaskOperator {

    private final Logger logger = LoggerFactory.getLogger(TaskSubmitFunction.class);

    @Override
    public Response apply(FlinkRequest flinkRequest) {
        SubmitResponse submitResponse = new SubmitResponse();
        submitResponse.success();
        if(Asserts.isNull(flinkRequest)){
            return null;
        }
        // 加载 submit 类
        String className = "";
        switch(flinkRequest.getRunModeType()){
            case LOCAL:
                className = "com.dpline.flink.submit.LocalSubmitter";
                break;
            case K8S_SESSION:
                className = "com.dpline.flink.submit.K8SSessionSubmitter";
                break;
            case K8S_APPLICATION:
            default:
                className = "com.dpline.flink.submit.K8SApplicationSubmitter";
        }
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            Method submitMethod = clazz.getDeclaredMethod("submit", FlinkRequest.class);
            submitMethod.setAccessible(true);
            submitResponse = (SubmitResponse) submitMethod.invoke(clazz.newInstance(), flinkRequest);
        } catch (Exception exception) {
            submitResponse.fail(ExceptionUtil.exceptionToString(exception));
            logger.error(ExceptionUtil.exceptionToString(exception));
        }
        return submitResponse;
    }
}
