package com.dpline.console.handler;

import com.dpline.common.util.*;
import com.dpline.console.service.impl.DplineJobOperateLogImpl;
import com.dpline.console.socket.FileDataListener;
import com.dpline.console.socket.JobLogTailTask;
import com.dpline.console.socket.WsSessionManager;
import com.dpline.console.util.SpringContextUtil;
import com.dpline.dao.entity.DplineJobOperateLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.concurrent.*;

public class LogSocketHandler implements WebSocketHandler {

    Long logId;

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

    public LogSocketHandler(Long logId){
        this.logId = logId;
    }

    /**
     * create new JobLogTailTask every trigger time。
     */
    @Override
    public void trigger() {
        DplineJobOperateLogImpl dplineJobOperateLogImpl = SpringContextUtil.getBean(DplineJobOperateLogImpl.class);
        DplineJobOperateLog dplineJobOperateLog = dplineJobOperateLogImpl.getMapper().selectById(logId);
        FileDataListener fileDataListener = new FileDataListener(WsSessionManager.get(this.logId));
        String logPath = dplineJobOperateLog.getOperateLogPath();
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
    }

}
