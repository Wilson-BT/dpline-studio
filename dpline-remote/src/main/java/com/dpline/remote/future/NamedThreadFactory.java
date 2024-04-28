package com.dpline.remote.future;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  thread factory
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger increment = new AtomicInteger(1);

    /**
     *  name
     */
    private final String name;

    /**
     *  count
     */
    private final int count;

    public NamedThreadFactory(String name){
        this(name, 0);
    }

    public NamedThreadFactory(String name, int count){
        this.name = name;
        this.count = count;
    }

    /**
     *  create thread
     * @param runnable runnable
     * @return thread
     */
    @Override
    public Thread newThread(Runnable runnable) {
        final String threadName = count > 0 ? String.format("%s_%d_%d", name, count, increment.getAndIncrement())
                : String.format("%s_%d", name, increment.getAndIncrement());
        Thread t = new Thread(runnable, threadName);
        t.setDaemon(true);
        return t;
    }
}
