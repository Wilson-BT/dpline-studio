package com.handsome.operator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:/operator.properties")
public class WatcherConfig {

    /**
     * flink请求restUrl的timeout时间
     */
    @Value("${watcher.rest.url.http.remote.timeout}")
    private int restUrlHttpRemoteTimeout;

    /**
     * None 超时变成lost状态的最大时长 10 * 1000
     */
    @Value("${watcher.none.status.change.max.time}")
    private int noneStatusChangeMaxTime;

    /**
     * batchFlushDataFromCacheToQueue 间隔多久从缓存刷新到队列中
     */
    @Value("${watcher.flush.queue.interval.seconds}")
    private int flushQueueIntervalSeconds;

    /**
     * 每个 k8s client 配置多少个对象供使用，需要使用循环队列去保存
     */
    @Value("${watcher.cache.k8s.client.num}")
    private int cacheK8sClientNum;

    /**
     * 多少个线程同时去消费队列
     */
    @Value("${watcher.consume.queue.thread.num}")
    private int consumeQueueThreadNum;

    /**
     * 间隔多少个小时刷新数据到缓存中
     */
    @Value("${watcher.flush.cache.interval.hours}")
    private int flushCacheIntervalHours;

    /**
     * 队列大小
     */
    @Value("${watcher.consume.queue.capacity}")
    private int consumeQueueCapacity;

    /**
     * 缓存经过多久过期
     */
    @Value("${watcher.cache.expire.max.hours}")
    private int cacheExpireMaxTime;

    /**
     * 缓存大小
     */
    @Value("${watcher.cache.size}")
    private int cacheSize;

    @Value("${watcher.alter.queue.capacity}")
    private int alterQueueCapacity;

    @Value("${watcher.alter.queue.offer.timeout}")
    private long alterQueueOfferTimeout;


    @Value("${watcher.alter.consume.thread.num}")
    private int alterConsumeThreadNum;


    public int getRestUrlHttpRemoteTimeout() {
        return restUrlHttpRemoteTimeout;
    }

    public void setRestUrlHttpRemoteTimeout(int restUrlHttpRemoteTimeout) {
        this.restUrlHttpRemoteTimeout = restUrlHttpRemoteTimeout;
    }

    public int getNoneStatusChangeMaxTime() {
        return noneStatusChangeMaxTime;
    }

    public void setNoneStatusChangeMaxTime(int noneStatusChangeMaxTime) {
        this.noneStatusChangeMaxTime = noneStatusChangeMaxTime;
    }

    public int getFlushQueueIntervalSeconds() {
        return flushQueueIntervalSeconds;
    }

    public void setFlushQueueIntervalSeconds(int flushQueueIntervalSeconds) {
        this.flushQueueIntervalSeconds = flushQueueIntervalSeconds;
    }

    public int getCacheK8sClientNum() {
        return cacheK8sClientNum;
    }

    public void setCacheK8sClientNum(int cacheK8sClientNum) {
        this.cacheK8sClientNum = cacheK8sClientNum;
    }

    public int getConsumeQueueThreadNum() {
        return consumeQueueThreadNum;
    }

    public void setConsumeQueueThreadNum(int consumeQueueThreadNum) {
        this.consumeQueueThreadNum = consumeQueueThreadNum;
    }

    public int getFlushCacheIntervalHours() {
        return flushCacheIntervalHours;
    }

    public void setFlushCacheIntervalHours(int flushCacheIntervalHours) {
        this.flushCacheIntervalHours = flushCacheIntervalHours;
    }

    public int getConsumeQueueCapacity() {
        return consumeQueueCapacity;
    }

    public void setConsumeQueueCapacity(int consumeQueueCapacity) {
        this.consumeQueueCapacity = consumeQueueCapacity;
    }

    public int getCacheExpireMaxTime() {
        return cacheExpireMaxTime;
    }

    public void setCacheExpireMaxTime(int cacheExpireMaxTime) {
        this.cacheExpireMaxTime = cacheExpireMaxTime;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
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

    public void setAlterQueueOfferTimeout(int alterQueueOfferTimeout) {
        this.alterQueueOfferTimeout = alterQueueOfferTimeout;
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
