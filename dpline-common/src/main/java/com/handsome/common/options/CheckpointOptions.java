package com.handsome.common.options;

import com.handsome.common.enums.CheckPointType;
import lombok.Data;

import java.io.Serializable;

@Data
public class CheckpointOptions implements Serializable {

    /**
     * 默认间隔
     */
//    @JsonProperty("execution.checkpointing.interval")
    private int defaultInterval;

    /**
     * 允许连续的checkpoint错误次数
     */
//    @JsonProperty("execution.checkpointing.tolerable-failed-checkpoints")
    private int tolerableFailNum;

    /**
     * EXACTLY_ONCE
     * AT_LEAST_ONCE
     */
//    @JsonProperty("execution.checkpointing.mode")
    private CheckPointType executionCheckPointMode;


    /**
     * DELETE_ON_CANCELLATION
     * RETAIN_ON_CANCELLATION
     * NO_EXTERNALIZED_CHECKPOINTS
     */
//    @JsonProperty("execution.checkpointing.externalized-checkpoint-retention")
    private String checkpointRetention;

    /**
     * 检查点目录
     */
//    @JsonProperty("state.checkpoints.dir")
    private String stateCheckpointsDir;


    private Boolean fromCheckpointAddress;


    private String checkpointAddress;


}
