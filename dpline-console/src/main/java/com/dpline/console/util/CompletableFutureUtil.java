package com.dpline.console.util;

//import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class CompletableFutureUtil{

    private final static ScheduledThreadPoolExecutor completableDelayer;

    static {
        completableDelayer = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory());
        completableDelayer.setRemoveOnCancelPolicy(true);
    }

    public static <T> CompletableFuture<Object> runTimeout(
        CompletableFuture<T> future,
        long timeout,
        TimeUnit unit) {

        return runTimeout(future, timeout, unit, null, null);
    }

    public static <T> CompletableFuture<Object> runTimeout(
        CompletableFuture<T> future,
        long timeout,
        TimeUnit unit,
        Consumer<T> handle,
        Consumer<Throwable> exceptionally) {

        return future
            .applyToEither(setTimeout(timeout, unit),
                t -> {
                    if (handle != null) {
                        handle.accept(t);
                    }
                    return null;
                })
            .exceptionally(t -> {
                if (exceptionally != null) {
                    exceptionally.accept(t);
                }
                return null;
            })
            .whenComplete((result, throwable) -> {
                if (!future.isDone()) {
                    future.cancel(true);
                }
            });
    }


    private static  <T> CompletableFuture<T> setTimeout(long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();
        completableDelayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    // Rest of the code...
    public void shutdown() {
        completableDelayer.shutdown();
    }
}
