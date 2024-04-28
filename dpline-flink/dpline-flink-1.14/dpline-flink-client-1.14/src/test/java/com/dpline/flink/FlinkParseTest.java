package com.dpline.flink;


//import com.dpline.flink.core.context.FlinkTableContext;
import com.dpline.common.enums.FlinkVersion;
import org.junit.Test;

import java.util.ArrayList;

public class FlinkParseTest {

    @Test
    public void parseSqls() throws Exception {
//        String path = "/tmp/tmp_dir";
//        String[] strings = {path};
//        Thread.currentThread().setContextClassLoader(
//                new JarClassLoader(strings,
//                        Thread.currentThread().getContextClassLoader()
//                )
//        );
        String createSourceTable = "CREATE TABLE IF NOT EXISTS `retail_pos`.`order_main_src` (\n" +
                "          `id` STRING NOT NULL,\n" +
                "          `company_no` STRING NULL,\n" +
                "          PRIMARY KEY(`id`)\n" +
                "         NOT ENFORCED\n" +
                "        ) WITH (\n" +
                "          'connector' = 'kafka',\n" +
                "          'format' = 'canal-json',\n" +
                "          'canal-json.ignore-parse-errors'='true',\n" +
                "          'topic' = 'flink_cdc_retail_pos_order_main',\n" +
                "          'properties.bootstrap.servers' = '10.250.128.17:9092,10.250.128.18:9092,10.250.128.19:9092',\n" +
                "          'properties.group.id' = 'flink-streamx-consumer',\n" +
                "          'scan.startup.mode' = 'group-offsets',\n" +
                "          'sink.semantic'='exactly-once'\n" +
                "        )";
        String createDatabase = "CREATE DATABASE IF NOT EXISTS `retail_pos`";
        String createSinkTable = "CREATE TABLE IF NOT EXISTS `retail_pos`.`order_main_sink` (\n" +
                "          `id` STRING NOT NULL,\n" +
                "          `company_no` STRING NULL,\n" +
                "          PRIMARY KEY(`id`)\n" +
                "         NOT ENFORCED\n" +
                "        ) with (\n" +
                "          'sink.properties.format' = 'json',\n" +
                "          'password' = 'retail_writer',\n" +
                "          'connector' = 'starrocks',\n" +
                "          'database-name' = 'test',\n" +
                "          'sink.buffer-flush.interval-ms' = '15000',\n" +
                "          'username' = 'retail_writer',\n" +
                "          'sink.properties.strip_outer_array' = 'true',\n" +
                "          'jdbc-url' = 'jdbc:mysql://10.250.148.63:9030',\n" +
                "          'load-url' = '10.250.148.63:8030',\n" +
                "          'table-name' = 'order_main'\n" +
                "        )";
        String insertSql = "INSERT INTO `retail_pos`.`order_main_sink` SELECT * FROM `retail_pos`.`order_main_src`";
        FlinkVersion flinkVersion = new FlinkVersion();
        flinkVersion.setMinorVersion(14);
        flinkVersion.setMajorVersion(1);

//        FlinkTableContext flinkTableContext = new FlinkTableContext();
        // yes is stream
//        flinkTableContext.open(Flag.YES,flinkVersion);
        ArrayList<String> str = new ArrayList<>();
        str.add(createDatabase);
        str.add(createSourceTable);
        str.add(createSinkTable);
        str.add(insertSql);
//        System.out.println(flinkTableContext.getStreamGraph(str));
    }
}
