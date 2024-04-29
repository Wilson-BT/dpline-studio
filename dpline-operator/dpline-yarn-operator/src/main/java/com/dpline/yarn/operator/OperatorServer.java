package com.dpline.yarn.operator;


import com.dpline.remote.NettyRemoteServer;
import com.dpline.remote.config.NettyServerConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;
import java.io.Closeable;

@ComponentScan(value = {
        "com.dpline.yarn.operator",
        "com.dpline.common",
        "com.dpline.alert",
        "com.dpline.dao"})
@MapperScan(value = {"com.dpline.dao.mapper"})
@EnableAutoConfiguration
public class OperatorServer implements Closeable {

    NettyRemoteServer server;

    private static final Logger logger = LoggerFactory.getLogger(OperatorServer.class);

    public static void main(String[] args) {
        Thread.currentThread().setName("OperatorServer");
        new SpringApplicationBuilder(OperatorServer.class)
                .web(WebApplicationType.NONE)
                .profiles("operator")
                .run(args);
    }

    /**
     * reFlush every day,
     *
     * @param event
     */
    @EventListener
    public void runWatcher(ApplicationReadyEvent event) {
        logger.info("Starting com.dpline.k8s.operator server");
        try {
            startServer();
            // 开启循环查询任务
//            taskStatusManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        NettyServerConfig serverConfig = new NettyServerConfig();
        server = new NettyRemoteServer(serverConfig);
        server.start();
        logger.info("NettyServer is open.");
        logger.info("Minio client is open.");
    }

    @PreDestroy
    @Override
    public void close() {
        server.close();
    }
}
