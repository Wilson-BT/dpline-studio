package com.dpline.yarn.operator;

import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.client.api.YarnClient;

// define hdfs and yarn client
@Data
public class Hadoop {

    /**
     * file system
     */
    private FileSystem fs;

    /**
     * configuration
     */
    private Configuration configuration;

    /**
     * yarn client
     */
    private YarnClient yarnClient;


}
