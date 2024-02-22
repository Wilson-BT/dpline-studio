package com.dpline.alert.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:alter.properties")
public class AlertConfig {

    @Value("${alter.queue.capacity}")
    private int alterQueueCapacity;

    @Value("${alter.queue.offer.timeout}")
    private long alterQueueOfferTimeout;

    @Value("${alter.consume.thread.num}")
    private int alterConsumeThreadNum;

    public AlertConfig() {
    }

    public int getAlterQueueCapacity() {
        return alterQueueCapacity;
    }

    public void setAlterQueueCapacity(int alterQueueCapacity) {
        this.alterQueueCapacity = alterQueueCapacity;
    }

    public long getAlterQueueOfferTimeout() {
        return alterQueueOfferTimeout;
    }

    public void setAlterQueueOfferTimeout(long alterQueueOfferTimeout) {
        this.alterQueueOfferTimeout = alterQueueOfferTimeout;
    }

    public int getAlterConsumeThreadNum() {
        return alterConsumeThreadNum;
    }

    public void setAlterConsumeThreadNum(int alterConsumeThreadNum) {
        this.alterConsumeThreadNum = alterConsumeThreadNum;
    }
}
