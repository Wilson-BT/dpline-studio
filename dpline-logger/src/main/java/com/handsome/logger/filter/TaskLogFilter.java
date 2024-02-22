package com.handsome.logger.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.handsome.logger.util.TaskConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskLogFilter extends Filter<ILoggingEvent> {

    private static Logger logger = LoggerFactory.getLogger(TaskLogFilter.class);

    /**
     * level
     */
    private Level level;

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    /**
     * Accept or reject based on thread name
     *
     * @param event event
     * @return FilterReply
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        FilterReply filterReply = FilterReply.DENY;
        if ((event.getThreadName().startsWith(TaskConstants.TASK_APPID_LOG_FORMAT)
            && event.getLoggerName().startsWith(TaskConstants.TASK_LOG_LOGGER_NAME))
            || event.getLevel().isGreaterOrEqual(level)) {
            filterReply = FilterReply.ACCEPT;
        }
        logger.debug("task log filter, thread name:{}, loggerName:{}, filterReply:{}, level:{}", event.getThreadName(), event.getLoggerName(), filterReply.name(), level);
        return filterReply;
    }
}
