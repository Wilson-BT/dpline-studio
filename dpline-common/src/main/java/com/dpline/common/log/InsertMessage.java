package com.dpline.common.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 插入doris 以及查询doris 的依赖
 */
@Data
public class InsertMessage implements Serializable {

    @JsonProperty("source")
    public String source;

    @JsonProperty("log_date")
    public String logDate;

    @JsonProperty("id")
    public String id;

    @JsonProperty("timestamp")
    public Long timestamp;

    @JsonProperty("content")
    public String content;

    @JsonProperty("host_ip")
    public String hostIp;

    @JsonProperty("method_name")
    public String methodName;

    @JsonProperty("level")
    public String level;

    @JsonProperty("file_name")
    public String fileName;

    @JsonProperty("line_number")
    public String lineNumber;

    @JsonProperty("thread_name")
    public String threadName;

    @JsonProperty("container_type")
    public String containerType;

    @JsonProperty("logger_name")
    public String loggerName;

    @JsonProperty("class_name")
    public String className;

    @JsonProperty("app_id")
    public String appId;

    @JsonProperty("host_name")
    public String hostName;

    @JsonProperty("container_id")
    public String containerId;


}
