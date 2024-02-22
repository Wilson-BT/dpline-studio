package com.handsome.operator;


import com.handsome.operator.process.*;
import com.handsome.operator.job.TaskStatusWatchManager;
import com.handsome.remote.command.CommandType;
import com.handsome.remote.config.NettyServerConfig;
import com.handsome.remote.NettyRemoteServer;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;
import java.io.Closeable;

@ComponentScan(value = {
    "com.handsome.operator",
    "com.handsome.alert",
    "com.handsome.dao"})
@MapperScan(value = {"com.handsome.dao.mapper"})
@EnableAutoConfiguration
public class OperatorServer implements Closeable {

    @Autowired
    TaskStatusWatchManager taskStatusChangeManager;

    @Autowired
    K8sClientAddProcessor k8sClientAddProcessor;

    @Autowired
    K8sClientRemoveProcessor k8sClientRemoveProcessor;

    @Autowired
    TaskRunProcessor taskRunProcessor;

    @Autowired
    TaskStopProcessor taskStopProcessor;


    NettyRemoteServer server;

    private static final Logger logger = LoggerFactory.getLogger(OperatorServer.class);

    public static void main(String[] args) {
        Thread.currentThread().setName("OperatorServer");
        new SpringApplicationBuilder(OperatorServer.class)
            .web(WebApplicationType.NONE)
            .profiles("com.handsome.operator")
            .run(args);
    }

    /**
     * reFlush every day,
     *
     * @param event
     */
    @EventListener
    public void runWatcher(ApplicationReadyEvent event) {
        logger.info("Starting com.handsome.operator server");
        try {
            startServer();
            taskStatusChangeManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        NettyServerConfig serverConfig = new NettyServerConfig();
        server = new NettyRemoteServer(serverConfig);
        server.registerProcessor(CommandType.TASK_RUN_REQUEST, taskRunProcessor);
        server.registerProcessor(CommandType.TASK_STOP_REQUEST, taskStopProcessor);
        server.registerProcessor(CommandType.K8S_CLIENT_ADD_REQUEST, k8sClientAddProcessor);
        server.registerProcessor(CommandType.K8S_CLIENT_REMOVE_REQUEST, k8sClientRemoveProcessor);
        server.start();
    }

    @PreDestroy
    @Override
    public void close() {
        server.close();
    }
}
