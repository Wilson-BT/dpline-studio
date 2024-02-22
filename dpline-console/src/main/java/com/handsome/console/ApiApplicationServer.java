package com.handsome.console;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.handsome.common.Constants;
import com.handsome.common.util.PropertyUtils;
import com.handsome.console.service.NettyClientService;
import com.handsome.remote.config.NettyServerConfig;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;


@ServletComponentScan
@SpringBootApplication(scanBasePackages={
        "com.handsome.console",
        "com.handsome.dao"})
public class ApiApplicationServer extends SpringBootServletInitializer {

    @Value("${spring.jackson.time-zone :UTC}")
    private String timezone;

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void close() {
        nettyClientService.close();
    }
}
