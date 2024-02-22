package com.dpline.dao.dto;

import lombok.Data;


@Data
public class JobRunConfig {

    private String runModeType;

    private String clusterName;

    private String motorType;

    private String motorVersion;

    private String imageName;

    private Integer defaultParallelism;

    private String jobManagerMemory;

    private String taskManagerMemory;

    private Double taskManagerCpus;

    private Double jobManagerCpus;

}
