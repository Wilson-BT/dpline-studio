package com.dpline.common.params;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * resources options for flink run
 */
@Data
public class RuntimeOptions implements Serializable {

    private Integer parallelism;

    private Double jobManagerCpu;

    private Double taskManagerCpu;

    private String jobManagerMem;

    private String taskManagerMem;

    private String taskManagerNum;

    Map<String,Object> otherParams = new HashMap<>();
}
