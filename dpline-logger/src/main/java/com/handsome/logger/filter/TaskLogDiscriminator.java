package com.handsome.logger.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import com.handsome.logger.util.TaskConstants;

/**
 * 任务日志描述，获取key 、value
 */
public class TaskLogDiscriminator extends AbstractDiscriminator<ILoggingEvent> {

    private static final String LOG_FILE_KEY = "TaskId";

    private static final String DEFAULT_VALUE = "unknown";

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        if (!iLoggingEvent.getLoggerName().startsWith(TaskConstants.TASK_LOG_LOGGER_NAME)) {
            return DEFAULT_VALUE;
        }
        return LOG_FILE_KEY;
    }

    @Override
    public String getKey() {
        return LOG_FILE_KEY;
    }
}
