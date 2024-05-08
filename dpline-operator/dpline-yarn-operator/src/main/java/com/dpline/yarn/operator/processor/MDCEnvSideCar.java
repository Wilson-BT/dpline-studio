package com.dpline.yarn.operator.processor;

import com.dpline.common.Constants;
import com.dpline.common.enums.OperationsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


public class MDCEnvSideCar {

    private Logger logger = LoggerFactory.getLogger(MDCEnvSideCar.class);

    /**
     * 初始化环境
     *
     * @param jobId
     * @param traceId
     * @param operationsEnum
     * @throws Exception
     */
    public void envInit(String jobId, String traceId, OperationsEnum operationsEnum) {
        logger.info("MDC ENV set jobId:[{}], traceId:[{}], operationsEnum:[{}].",jobId, traceId, operationsEnum);
        MDC.clear();
        MDC.put(Constants.TRACE_ID, traceId);
        MDC.put(Constants.JOB_ID, jobId);
        MDC.put(Constants.RUN_TYPE, operationsEnum.getOperateType());
    }


    /**
     * 重新恢复
     */
    public void revertEnv(){
        MDC.clear();
        logger.info("MDC ENV cleared.");
    }

}
