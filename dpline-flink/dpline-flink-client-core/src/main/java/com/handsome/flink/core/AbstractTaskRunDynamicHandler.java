package com.handsome.flink.core;

import com.handsome.common.request.Request;
import com.handsome.common.request.Response;
import com.handsome.flink.core.util.FlinkVersionClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractTaskRunDynamicHandler {

    private final Logger logger = LoggerFactory.getLogger(AbstractTaskRunDynamicHandler.class);

    private final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    final static FlinkVersionClassLoader flinkVersionClassLoadUtil = new FlinkVersionClassLoader();

    /**
     * 切换 classLoader，执行任务
     */
    public Response runAsClassLoader(Request request) {
        ClassLoader targetClassLoader = flinkVersionClassLoadUtil.getFlinkClientClassLoader(request.getFlinkHomeOptions());
        Thread.currentThread().setContextClassLoader(targetClassLoader);
        Response remoteResponse = null;
        try {
             remoteResponse = execFunc(targetClassLoader,request);
        } catch (Exception e) {
            logger.error("Target class exec failed.");
            throw new RuntimeException(e.toString());
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return remoteResponse;
    }

    public abstract Response execFunc(ClassLoader targetClassLoader, Request submitRequest) throws Exception;


}
