package com.dpline.console;

import com.dpline.console.service.NettyClientService;
import com.dpline.common.store.Minio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TimeZone;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = {
        "com.dpline.console",
        "com.dpline.common",
        "com.dpline.remote",
        "com.dpline.dao",
        "com.dpline.datasource"})
public class ApiApplicationServer extends SpringBootServletInitializer {

    @Value("${spring.jackson.time-zone}")
    private String timezone;

    private static Logger logger = LoggerFactory.getLogger(ApiApplicationServer.class);

    @Autowired
    Minio minio;

    @Autowired
    NettyClientService nettyClientService;

    public static void main(String[] args) {
        SpringApplication.run(ApiApplicationServer.class);
    }

    @PostConstruct
    public void run() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
    }


    @EventListener
    public void startRemoteClient(ApplicationReadyEvent event) {
        try {
            nettyClientService.startClient();
            logger.info("Netty sever start.");
            minio.createMinioClient();
            logger.info("Minio sever start.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void close() {
        nettyClientService.close();
    }
}
