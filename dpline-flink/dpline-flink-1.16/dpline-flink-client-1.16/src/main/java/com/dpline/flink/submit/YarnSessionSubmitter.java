package com.dpline.flink.submit;

import com.dpline.common.request.FlinkSubmitRequest;
import org.apache.flink.configuration.Configuration;

public class YarnSessionSubmitter extends AbstractConfigSetting{
    @Override
    public void setSpecialConfig(Configuration configuration, FlinkSubmitRequest flinkSubmitRequest) {

    }
}
