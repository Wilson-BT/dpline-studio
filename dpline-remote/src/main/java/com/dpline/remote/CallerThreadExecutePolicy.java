package com.dpline.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CallerThreadExecutePolicy implements RejectedExecutionHandler {

    private final Logger logger = LoggerFactory.getLogger(CallerThreadExecutePolicy.class);

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        logger.warn("queue is full, trigger caller thread execute");
        runnable.run();
    }
}
