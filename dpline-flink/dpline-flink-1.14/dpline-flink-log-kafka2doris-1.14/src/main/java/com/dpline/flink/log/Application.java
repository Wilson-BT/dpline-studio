package com.dpline.flink.log;

import cn.hutool.core.bean.BeanUtil;
import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.sink.DorisSink;
import org.apache.doris.flink.sink.writer.SimpleStringSerializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.kafka.shaded.org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.flink.shaded.netty4.io.netty.util.internal.StringUtil;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class Application {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static JobProperties jobProperties = new JobProperties();

    public static void main(String[] args) {
        jobProperties.init(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers(jobProperties.getKafkaConsumerBootstrapServers())
            .setTopics(jobProperties.getKafkaConsumerTopic())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .setGroupId(jobProperties.getKafkaConsumerGroupId())
            .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
            .build();

        DataStreamSource<String> logMessageDataStreamSource = env.fromSource(kafkaSource,
            WatermarkStrategy.noWatermarks(),
            "KAFKA-SOURCE"
        );
        // 日志
        SingleOutputStreamOperator<String> mapDataStream = logMessageDataStreamSource.map(
            new MapFunction<String, String>() {
                @Override
                public String map(String value) throws Exception {
                    LogMessage logMessage = JSONUtils.parseObject(value, LogMessage.class);
                    if (logMessage == null) {
                        return null;
                    }

                    if(logMessage.getId() == null){
                        return null;
                    }
                    InsertMessage insertMessage = new InsertMessage();
                    BeanUtil.copyProperties(logMessage, insertMessage);
                    LogMessage.Tag tag = logMessage.getTags();
                    if(tag == null){
                        return null;
                    }
                    insertMessage.setHostIp(tag.getHostIp());
                    Date date = new Date(insertMessage.getTimestamp());
                    insertMessage.setLogDate(dateFormat.format(date));
                    insertMessage.setMethodName(tag.getMethodName());
                    insertMessage.setLevel(tag.getLevel());
                    insertMessage.setFileName(tag.getFileName());
                    insertMessage.setLineNumber(tag.getLineNumber());
                    insertMessage.setThreadName(tag.getThreadName());
                    insertMessage.setContainerType(tag.getContainerType());
                    insertMessage.setLoggerName(tag.getLoggerName());
                    insertMessage.setClassName(tag.getClassName());
                    insertMessage.setAppId(tag.getAppId());
                    insertMessage.setHostName(tag.getHostName());
                    insertMessage.setContainerId(tag.getContainerId());
                    return JSONUtils.toJsonString(insertMessage);
                }
            }
        ).filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                if (StringUtil.isNullOrEmpty(value)){
                    return false;
                }
                return true;
            }
        });
        DorisSink.Builder<String> builder = DorisSink.builder();
        DorisOptions dorisOptions = DorisOptions.builder()
            .setFenodes(jobProperties.getDorisFeNodeList())
            .setTableIdentifier(jobProperties.getDorisTableIdentifierName())
            .setUsername(jobProperties.getDorisUsername())
            .setPassword(jobProperties.getDorisPassword())
            .build();
        Properties properties = new Properties();
        properties.setProperty("format", "json");
        properties.setProperty("read_json_by_line", "true");

        DorisExecutionOptions build = DorisExecutionOptions.builder()
            .setMaxRetries(3)
            .disable2PC()
            .setStreamLoadProp(properties)
            .build();
        builder.setDorisReadOptions(DorisReadOptions.builder().build())
            .setDorisExecutionOptions(build)
            .setSerializer(new SimpleStringSerializer()) //serialize according to string
            .setDorisOptions(dorisOptions);

        mapDataStream.sinkTo(builder.build());
        try {
            env.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
