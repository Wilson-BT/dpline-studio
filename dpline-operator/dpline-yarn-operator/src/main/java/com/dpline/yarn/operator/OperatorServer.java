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
        "com.dpline.yarn.operator",
        "com.dpline.operator",
        "com.dpline.common",
        "com.dpline.alert",
        "com.dpline.dao"})
@MapperScan(value = {"com.dpline.dao.mapper"})
@EnableAutoConfiguration
public class OperatorServer implements Closeable {
    NettyRemoteServer server;

    @Autowired
    TaskStatusManager taskStatusManager;
    @Autowired
    TaskRunProcessor taskRunProcessor;
    @Autowired
    TaskStopProcessor taskStopProcessor;
    @Autowired
    TestPingProcessor testPingProcessor;
    @Autowired
    YarnClientAddProcessor yarnClientAddProcessor;
    @Autowired
    YarnClientRemoveProcessor yarnClientRemoveProcessor;
    @Autowired
    YarnClientUpdateProcessor yarnClientUpdateProcessor;
    @Autowired
    TaskTriggerProcessor taskTriggerProcessor;
    @Autowired
    TaskAlertEditProcessor TaskAlertEditProcessor;
    @Autowired
    FileDagProcessor fileDagProcessor;
    @Autowired
    TaskAlertEditProcessor taskAlertEditProcessor;

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
        server.registerProcessor(CommandType.PING, testPingProcessor);
        server.registerProcessor(CommandType.CLIENT_ADD_REQUEST, yarnClientAddProcessor);
        server.registerProcessor(CommandType.CLIENT_REMOVE_REQUEST, yarnClientRemoveProcessor);
        server.registerProcessor(CommandType.CLIENT_UPDATE_REQUEST, yarnClientUpdateProcessor);
        server.registerProcessor(CommandType.TASK_TRIGGER_REQUEST, taskTriggerProcessor);
        server.registerProcessor(CommandType.TASK_RUN_REQUEST, taskRunProcessor);
        server.registerProcessor(CommandType.TASK_STOP_REQUEST, taskStopProcessor);
        server.registerProcessor(CommandType.TASK_ALERT_EDIT_REQUEST, taskAlertEditProcessor);
        server.registerProcessor(CommandType.FILE_DAG_REQUEST,fileDagProcessor);
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
