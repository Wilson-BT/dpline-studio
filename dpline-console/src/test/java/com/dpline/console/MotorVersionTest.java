package com.dpline.console;

import com.dpline.common.enums.RunMotorType;
import com.dpline.console.handler.MotorVersionHandler;
import org.junit.Test;

import java.io.IOException;


public class MotorVersionTest {

    @Test
    public void paserMotor() throws IOException {
        String s = MotorVersionHandler.parseJarMotorVersion("/Users/wangchunshun/PycharmProjects/flink-client/jars/flink-114/main-jars/binlog-topic-database-sync-1.1.jar", RunMotorType.FLINK);
        System.out.println(s);
    }


}
