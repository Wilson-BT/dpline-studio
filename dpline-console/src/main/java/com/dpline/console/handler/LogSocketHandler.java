package com.dpline.console.handler;

import com.dpline.common.util.*;
import com.dpline.console.service.impl.DplineJobOperateLogImpl;
import com.dpline.console.socket.FileDataListener;
import com.dpline.console.socket.JobLogTailTask;
import com.dpline.console.socket.WebSocketEndpoint;
import com.dpline.console.util.SpringContextUtil;
import com.dpline.dao.entity.DplineJobOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.concurrent.*;

public class LogSocketHandler implements WebSocketHandler {

    private Long logId;

    private String logPath;

    private JobLogTailTask jobLogTailTask;

    private CompletableFuture<Void> voidCompletableFuture;

    static final ExecutorService executorService = new ThreadPoolExecutor(
        2,
        10,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(1024),
        ThreadUtils.threadFactory("Log-tail-executor"),
        new ThreadPoolExecutor.AbortPolicy());

    private Logger logger = LoggerFactory.getLogger(LogSocketHandler.class);

    @Override
    public void init(Long logId){
        this.logId = logId;
        DplineJobOperateLogImpl dplineJobOperateLogImpl = SpringContextUtil.getBean(DplineJobOperateLogImpl.class);
        DplineJobOperateLog dplineJobOperateLog = dplineJobOperateLogImpl.getMapper().selectById(logId);
        this.logPath = dplineJobOperateLog.getOperateLogPath();
    }

    /**
     * 读取部署日志
     */
    @Override
    public void trigger(WebSocketEndpoint webSocketEndpoint) {
        FileDataListener fileDataListener = new FileDataListener(webSocketEndpoint);
        if(StringUtils.isEmpty(logPath)){
            return;
        }
        jobLogTailTask = new JobLogTailTask(
            new File(logPath),
            fileDataListener,
            1000L
        );
        voidCompletableFuture = CompletableFutureUtils.orTimeout(
            CompletableFuture.runAsync(jobLogTailTask, executorService),
            20000L, TimeUnit.MILLISECONDS
        ).exceptionally((ex) -> {
            logger.error("读取超时关闭。。。");
            this.close();
            return null;
        });
    }

    @Override
    public void close() {
        logger.info("日志读取任务正在关闭");
        if(Asserts.isNotNull(voidCompletableFuture) && !voidCompletableFuture.isDone()){
            voidCompletableFuture.cancel(true);
        }
        if(Asserts.isNotNull(jobLogTailTask)){
            jobLogTailTask.stop();
        }
        this.logId = null;
        this.logPath = null;
    }

//    static class LogThreadPool {

//        static class LogTailThreadFactory implements ThreadFactory {
//
//            private final String namePrefix;
//
//            public LogTailThreadFactory(String namePrefix) {
//                this.namePrefix = namePrefix;
//            }
//
//            @Override
//            public Thread newThread(@NotNull Runnable runnable) {
//                Thread thread = new Thread(runnable);
//                thread.setDaemon(true);
//                thread.setName(namePrefix + "-" + thread.getId());
//                return thread;
//            }
//        }
//    }
}
