package com.dpline.k8s.operator;

import com.dpline.common.enums.ClusterType;
import com.dpline.k8s.operator.process.*;
import com.dpline.k8s.operator.watcher.TaskStatusManager;
import com.dpline.operator.processor.FileDagProcessor;
import com.dpline.remote.command.CommandType;
import com.dpline.remote.config.NettyServerConfig;
import com.dpline.remote.NettyRemoteServer;
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
    "com.dpline.k8s.operator",
    "com.dpline.operator",
    "com.dpline.common",
    "com.dpline.alert",
    "com.dpline.dao"})
@MapperScan(value = {"com.dpline.dao.mapper"})
@EnableAutoConfiguration
public class OperatorServer implements Closeable {

    @Autowired
    TaskStatusManager taskStatusManager;

    @Autowired
    TaskTriggerProcessor taskTriggerProcessor;

    @Autowired
    K8sClientAddProcessor k8sClientAddProcessor;

    @Autowired
    K8sClientRemoveProcessor k8sClientRemoveProcessor;

    @Autowired
    TaskRunProcessor taskRunProcessor;

    @Autowired
    TaskStopProcessor taskStopProcessor;

    @Autowired
    FileDagProcessor fileDagProcessor;

    @Autowired
    TaskAlertEditProcessor taskAlertEditProcessor;

    @Autowired
    K8sClientUpdateProcessor k8sClientUpdateProcessor;


    NettyRemoteServer server;

    private static final Logger logger = LoggerFactory.getLogger(OperatorServer.class);

    public static void main(String[] args) {
        Thread.currentThread().setName("OperatorServer");
        new SpringApplicationBuilder(OperatorServer.class)
            .web(WebApplicationType.NONE)
            .profiles("k8s-operator")
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
        server.registerProcessor(CommandType.TASK_RUN_REQUEST, taskRunProcessor);
        server.registerProcessor(CommandType.TASK_STOP_REQUEST, taskStopProcessor);
        server.registerProcessor(CommandType.TASK_ALERT_EDIT_REQUEST, taskAlertEditProcessor);
        // 集群添加
        server.registerProcessor(CommandType.CLIENT_ADD_REQUEST, k8sClientAddProcessor);
        // 集群移除
        server.registerProcessor(CommandType.CLIENT_REMOVE_REQUEST, k8sClientRemoveProcessor);
        // 集群更新
        server.registerProcessor(CommandType.CLIENT_UPDATE_REQUEST,k8sClientUpdateProcessor);
        server.registerProcessor(CommandType.FILE_DAG_REQUEST, fileDagProcessor);
        server.registerProcessor(CommandType.TASK_TRIGGER_REQUEST,taskTriggerProcessor);
        server.start(ClusterType.KUBERNETES);
        logger.info("NettyServer is open.");
    }

    @PreDestroy
    @Override
    public void close() {
        server.close();
    }
}
