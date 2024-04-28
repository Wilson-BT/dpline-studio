
package com.dpline.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.dpline.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *  task log filter
 */
public class JobLogFilter extends Filter<ILoggingEvent> {

    private static Logger logger = LoggerFactory.getLogger(JobLogFilter.class);
    /**
     * level
     */
    private Level level;

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    /**
     * Accept or reject based on thread name
     * @param event event
     * @return FilterReply
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        FilterReply filterReply = FilterReply.DENY;
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        if (mdcPropertyMap.containsKey(Constants.TRACE_ID) && mdcPropertyMap.containsKey(Constants.JOB_ID) && event.getLevel().isGreaterOrEqual(level)){
            filterReply = FilterReply.ACCEPT;
        }
//        logger.debug("task log filter, thread name:{},loggerName:{},filterReply:{}", event.getThreadName(), event.getLoggerName(), filterReply.name());
        return filterReply;
    }
}