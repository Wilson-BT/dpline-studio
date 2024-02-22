package com.dpline.common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import com.dpline.common.Constants;
import com.dpline.common.enums.OperationsEnum;
import com.dpline.common.util.TaskPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JobLogDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

    private static Logger logger = LoggerFactory.getLogger(JobLogDiscriminator.class);

    private static final String DEFAULT_TRACE_ID = "0000000000000";

    private static final String DEFAULT_RUN_TYPE = OperationsEnum.DEPLOY.getOperateType();

    /**
     * key
     */
    private String key;

    /**
     * log base
     */
    private String logBase;


    /**
     * 获取 日志文件的绝对路径
     * @param event
     * @return
     */
    @Override
    public String getDiscriminatingValue(ILoggingEvent event) {
        String jobId = DEFAULT_TRACE_ID;
        String traceId = DEFAULT_TRACE_ID;
        String runType = DEFAULT_RUN_TYPE;
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        if(mdcPropertyMap.containsKey(Constants.JOB_ID)){
           jobId = mdcPropertyMap.get(Constants.JOB_ID);
        }
        if(mdcPropertyMap.containsKey(Constants.TRACE_ID)){
            traceId = mdcPropertyMap.get(Constants.TRACE_ID);
        }
        if(mdcPropertyMap.containsKey(Constants.RUN_TYPE)){
            runType = mdcPropertyMap.get(Constants.RUN_TYPE);
        }
        return TaskPathResolver.getJobLogPath(runType,jobId,traceId);
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogBase() {
        return logBase;
    }

    public void setLogBase(String logBase) {
        this.logBase = logBase;
    }
}
