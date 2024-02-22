package com.dpline.flink.core.context;

import lombok.Builder;
import lombok.Data;

/**
 * common session
 */
@Builder
@Data
public class SessionStudio {

    private SessionId sessionId;

    private Long lastActiveTime;

    private AbstractSessionContext sessionContext;

    public void updateLastActiveTime(Long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
}
