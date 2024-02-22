package com.dpline.common.util;

import com.dpline.common.params.JobConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class JobConfigSerialization {

    private static Logger logger = LoggerFactory.getLogger(JobConfigSerialization.class);


    public static Map<String, Object> parseYamlToMap(String flinkYaml) {
        LinkedHashMap<String, Object> options = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(flinkYaml)) {
            Yaml yaml = new Yaml();
            Map obj = yaml.loadAs(DeflaterUtils.unzipString(flinkYaml),Map.class);
            options.putAll(obj);
        }
        return options;
    }


    /**
     * 解析 ConfigContent 为 JobConfig
     * @return
     */
    public static JobConfig parseJobConfig(String configContent){
        return JSONUtils.parseObject(configContent, JobConfig.class);
    }

    public static String unzipJobContent(String jobContent){
        return DeflaterUtils.unzipString(jobContent);
    }

    public String zipJobContent(String jobContent){
        return DeflaterUtils.zipString(jobContent);
    }


}
