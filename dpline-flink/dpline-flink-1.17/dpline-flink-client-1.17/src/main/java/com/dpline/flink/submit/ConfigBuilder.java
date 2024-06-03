package com.dpline.flink.submit;

import com.dpline.common.request.FlinkK8sRemoteSubmitRequest;
import org.apache.hadoop.conf.Configuration;

public class ConfigBuilder {

    private FlinkK8sRemoteSubmitRequest submitRequest;

    private Configuration configuration;

    public ConfigBuilder(FlinkK8sRemoteSubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
    }

    public Configuration init() {


        return configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
