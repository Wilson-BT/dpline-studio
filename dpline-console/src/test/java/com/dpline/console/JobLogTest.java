package com.dpline.console;

import com.dpline.console.handler.LogSocketHandler;
import com.dpline.console.socket.WebSocketEndpointHandler;
import com.dpline.dao.entity.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Timer;
import java.util.TimerTask;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JobLogTest {

    @Autowired
    WebSocketEndpointHandler webSocketEndpointHandler;

    private static final Logger logger = LoggerFactory.getLogger(JobLogTest.class);

    @Test
    public void exampleMethod() {
        MDC.put("projectId", "egterghtr");
        MDC.put("jobId", "fsghtrdh");
        MDC.put("deployType", "fshyrtjy");

        logger.info("Log message");

        MDC.clear();
    }

    /**
     * 超时退出机制测试
     */
    @Test
    public void timeoutTest() throws InterruptedException {
        Timer timer = new Timer();
        TimerTask timeoutTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("任务超时了");
                timer.cancel();
            }
        };
        // 10s 之后

        int count = 100;
        for (int i = 0; i<=count ;i++) {
            if(i==0){
                timer.schedule(timeoutTask, 5000L);
            }
            System.out.println(count);
            Thread.sleep(1000L);
        }
        System.out.println("超时结束。。。");
    }

    public void cancelCompletAbleFuture(){
        Job job = new Job();
        job.setId(10668357750176L);
        job.setProjectId(9640278031136L);
        LogSocketHandler deployLogSocketHandler = new LogSocketHandler(10668357750176L);
//        webSocketEndpoint.onOpen();
    }


}
