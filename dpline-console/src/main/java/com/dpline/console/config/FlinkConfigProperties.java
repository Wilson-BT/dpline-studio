package com.dpline.console.config;

import com.dpline.common.util.Asserts;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;

@Component
@Data
public class FlinkConfigProperties {

    private final static String DATA_STREAM_YML = "flink-dstream-template.yml";

    private final static String SQL_STREAM_YML = "flink-sql-template.yml";

    private String sqlTemplate;

    private String dStreamTemplate;

    public FlinkConfigProperties() throws IOException {
        initSqlTemplate();
        initDStreamTemplate();
    }

    /**
     * 初始化DataStream
     */
    private void initDStreamTemplate() throws IOException {
        this.dStreamTemplate = getYmlText(DATA_STREAM_YML);
    }

    /**
     * 初始化Sql参数
     */
    private void initSqlTemplate() throws IOException {
        this.sqlTemplate = getYmlText(SQL_STREAM_YML);
    }

    private String getYmlText(String path) throws IOException {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if(Asserts.isNull(resourceStream)){
            throw new IOException(String.format("File %s not found.", path));
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append("\n").append(line);
        }
        return stringBuilder.toString();
    }
}
