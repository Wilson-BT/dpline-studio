package com.handsome.common.options;

import lombok.Data;

import java.io.Serializable;

/**
 * resources options for flink run
 */
@Data
public class ResourceOptions implements Serializable {

//  @JsonProperty("taskmanager.memory.process.size")
    private String taskmanagerMemoryProcessSize;

//    @JsonProperty("jobmanager.memory.process.size")
    private String jobmanagerMemoryProcessSize;
    /**
     * Parallelism
     */
//    @TableField("parallelism.default")
    private int parallelism;

    private double jobManagerCpu;

    private double taskManagerCpu;

//    @JsonProperty("taskmanager.numberOfTaskSlots")
    private int taskSlots;
}
