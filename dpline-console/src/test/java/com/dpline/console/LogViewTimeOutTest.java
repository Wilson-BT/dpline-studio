package com.dpline.console;

import com.dpline.common.util.CompletableFutureUtils;
import com.dpline.common.util.StringUtils;
import com.dpline.console.socket.FileDataListener;
import com.dpline.console.socket.JobLogTailTask;
import com.dpline.console.util.CompletableFutureUtil;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LogViewTimeOutTest {

    @Test
    public void timeOut() {
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                System.out.println("我要开始了。。。");
                try {
                    while (true){
                        Thread.sleep(1000L);
                        System.out.println("lalalal");
                    }
                } catch (Exception e) {
                    System.out.println("heihie,我被打断了");
                    e.printStackTrace();
                } finally {
                    System.out.println("我结束了。。。");
                }
            }
        };
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(thread);
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFutureUtils.orTimeout(voidCompletableFuture, 5, TimeUnit.SECONDS);
        try {
            voidCompletableFuture1.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("准备退出");
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void timeOutTest() {
        String logPath = "/tmp/dpline/logs/9640278031136/deploy/11042017247904.log";
        TailerListener thread = new TailerListener() {

            @Override
            public void init(Tailer tailer) {
//                System.out.println("init");
            }

            @Override
            public void fileNotFound() {
                System.out.println("fileNotFound");
            }

            @Override
            public void fileRotated() {
                System.out.println("fileRotated");
            }

            @Override
            public void handle(String s) {
                System.out.println("handleS");
            }

            @Override
            public void handle(Exception e) {
                System.out.println("handleEx");
            }
        };
        if(StringUtils.isEmpty(logPath)){
            return;
        }
        JobLogTailTask jobLogTailTask = new JobLogTailTask(
            new File(logPath),
            thread,
            1000L
        );
        CompletableFuture<Void> voidCompletableFuture = CompletableFutureUtils.orTimeout(
            CompletableFuture.runAsync(jobLogTailTask),
            10000L, TimeUnit.MILLISECONDS
        );
        try {
            voidCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("准备退出");
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
