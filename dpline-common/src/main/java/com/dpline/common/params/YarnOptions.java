package com.dpline.common.params;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class YarnOptions {

    /**
     * hadoop local home path
     */
    private String HadoopHome;

    /**
     * flink home path on hdfs
     */
    private List<String> flinkJarDirPath;


    private String flinkDistJarPath;


}
