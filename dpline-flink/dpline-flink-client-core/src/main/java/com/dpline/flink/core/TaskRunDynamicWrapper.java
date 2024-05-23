package com.dpline.flink.core;

import com.dpline.common.request.FlinkRequest;
import com.dpline.common.util.ExceptionUtil;
import com.dpline.flink.core.util.FlinkVersionClassLoader;
import com.dpline.common.request.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaskRunDynamicWrapper {

    private final Logger logger = LoggerFactory.getLogger(TaskRunDynamicWrapper.class);

    final static FlinkVersionClassLoader flinkVersionClassLoadUtil = new FlinkVersionClassLoader();

    private final TaskOperator functionExecutor;

    public TaskRunDynamicWrapper(TaskOperator operator) {
        this.functionExecutor = operator;
    }

    /**
     * 切换 classLoader，执行任务
     */
    public synchronized Response execute(FlinkRequest request) throws Exception {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        // add all flink jars,create and set with a new ClassLoader
        ChildFirstClassLoader targetClassLoader = flinkVersionClassLoadUtil.getFlinkClientClassLoader(request);
        Thread.currentThread().setContextClassLoader(targetClassLoader);
        logger.info("Change classLoader...");
        Response response = null;
        try {
            response = this.functionExecutor.apply(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            logger.info("Reset classLoader...");
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return response;
    }
}
