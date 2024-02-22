package com.handsome.common.options;

import com.handsome.common.enums.RestartStrategy;
import lombok.Data;

import java.io.Serializable;

@Data
public class RestartOptions implements Serializable {

//    @JsonProperty("restart-strategy")
    private RestartStrategy restartStrategy;

    /**
     * 重试次数
     */
//    @JsonProperty("restart-strategy.fixed-delay.attempts")
    private int attemptTimes;

    /**
     * 延迟时间
     */
//    @JsonProperty("restart-strategy.fixed-delay.delay")
    private long delayTime;

}
