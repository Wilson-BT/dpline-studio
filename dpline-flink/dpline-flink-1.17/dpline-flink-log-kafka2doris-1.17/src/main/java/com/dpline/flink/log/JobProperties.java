package com.dpline.flink.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class JobProperties {

    private final static String KAFKA_CONSUMER_BOOTSTRAP_SERVERS = "kafka.consumer.bootstrap.servers";

    private final static String KAFKA_CONSUMER_TOPIC = "kafka.consumer.topic";

    private final static String KAFKA_CONSUMER_GROUP_ID = "kafka.consumer.group.id";

    private final static String DORIS_FE_NODE_LIST = "doris.fe.node.list";

    private final static String DORIS_TABLE_IDENTIFIER_NAME = "doris.table.identifier.name";

    private final static String DORIS_USERNAME = "doris.username";

    private final static String DORIS_PASSWORD = "doris.password";


    private final Logger logger = LoggerFactory.getLogger(JobProperties.class);

    private Properties defaultProperties = new Properties();
    public void init(String [] args){
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("arguments are Illegal, please check again. ");
        }
        try {
            defaultProperties.load(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; (i * 2 + 1) < args.length; i++) {
            defaultProperties.put(args[i * 2].replace("--",""), args[i * 2 + 1]);
        }
        logger.info("解析参数为: [{}]",defaultProperties);
    }

    public Integer getPropertyInt(String argName){
        return Integer.parseInt(defaultProperties.getProperty(argName));
    }

    public String getPropertyString(String argName){
        return defaultProperties.getProperty(argName);
    }

    public String getKafkaConsumerBootstrapServers(){
        return defaultProperties.getProperty(KAFKA_CONSUMER_BOOTSTRAP_SERVERS);
    }

    public String getKafkaConsumerTopic(){
        return defaultProperties.getProperty(KAFKA_CONSUMER_TOPIC);
    }

    public String getKafkaConsumerGroupId(){
        return defaultProperties.getProperty(KAFKA_CONSUMER_GROUP_ID);
    }

    public String getDorisFeNodeList(){
        return defaultProperties.getProperty(DORIS_FE_NODE_LIST);
    }

    public String getDorisTableIdentifierName(){
        return defaultProperties.getProperty(DORIS_TABLE_IDENTIFIER_NAME);
    }

    public String getDorisUsername(){
        return defaultProperties.getProperty(DORIS_USERNAME);
    }
    public String getDorisPassword(){
        return defaultProperties.getProperty(DORIS_PASSWORD);
    }

}
