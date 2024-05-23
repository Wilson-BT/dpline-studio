package com.dpline.operator.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;



@Data
public class SniffEvent implements Delayed {

    /**
     * clusterEntityId
     */
    Long clusterId;

    /**
     * applicationSessionId
     */
    String applicationId;

    /**
     * expire time
     */
    Long expireTime;

    public SniffEvent(Long clusterId, String applicationId) {
        this.clusterId = clusterId;
        this.applicationId = applicationId;
    }

    public void resetExpireTime(long delayTime, TimeUnit delayTimeUnit){
        this.expireTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delayTime, delayTimeUnit);
    }

    /**
     * 剩余时间
     * @param time
     * @return
     */
    @Override
    public long getDelay(@NotNull TimeUnit time) {
        return time.convert(this.expireTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 优先级:俩个任务比较，时间短的优先执行
     *
     */
    @Override
    public int compareTo(Delayed o){
        long f = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return (int)f;
    }
}
