package com.dpline.yarn.operator;


import com.dpline.common.enums.ClusterType;
import com.dpline.operator.processor.*;
import com.dpline.remote.NettyRemoteServer;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.config.NettyServerConfig;
import com.dpline.yarn.operator.processor.*;
import com.dpline.yarn.operator.watcher.TaskStatusManager;
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
        "com.dpline.operator",
        "com.dpline.common",
        "com.dpline.alert",
        "com.dpline.dao"})
@MapperScan(value = {"com.dpline.dao.mapper"})
@EnableAutoConfiguration
public class OperatorServer implements Closeable {
    NettyRemoteServer server;

    TaskStatusManager taskStatusManager;

    private static final Logger logger = LoggerFactory.getLogger(OperatorServer.class);

    public static void main(String[] args) {
        Thread.currentThread().setName("OperatorServer");
        new SpringApplicationBuilder(OperatorServer.class)
                .web(WebApplicationType.NONE)
                .profiles("yarn-operator")
                .run(args);
    }

    /**
     * reFlush every day,
     *
     * @param event
     */
    @EventListener
    public void runWatcher(ApplicationReadyEvent event) {
        logger.info("Starting com.dpline.yarn.operator server");
        try {
            startServer();
            // 开启循环查询任务
            taskStatusManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        NettyServerConfig serverConfig = new NettyServerConfig();
        server = new NettyRemoteServer(serverConfig);
        server.registerProcessor(CommandType.PING, new TestPingProcessor());
        server.registerProcessor(CommandType.CLIENT_ADD_REQUEST, new YarnClientAddProcessor());
        server.registerProcessor(CommandType.CLIENT_REMOVE_REQUEST, new YarnClientRemoveProcessor());
        server.registerProcessor(CommandType.CLIENT_UPDATE_REQUEST, new YarnClientUpdateProcessor());
        server.registerProcessor(CommandType.TASK_TRIGGER_REQUEST, new TaskTriggerProcessor());
        server.registerProcessor(CommandType.TASK_RUN_REQUEST, new TaskRunProcessor());
        server.registerProcessor(CommandType.TASK_STOP_REQUEST, new TaskStopProcessor());
        server.registerProcessor(CommandType.TASK_ALERT_EDIT_REQUEST, new TaskAlertEditProcessor());
        server.registerProcessor(CommandType.FILE_DAG_REQUEST,new FileDagProcessor());
        server.start(ClusterType.YARN);
        logger.info("NettyServer is opened.");
    }

    @PreDestroy
    @Override
    public void close() {
        logger.info("NettyServer is closed.");
        server.close();
    }
}
