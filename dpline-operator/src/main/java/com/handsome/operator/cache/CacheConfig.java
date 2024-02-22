package com.handsome.operator.cache;

import com.handsome.operator.config.WatcherConfig;
import com.handsome.operator.job.ClusterFlushEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Autowired
    WatcherConfig watcherConfig;

    @Bean("clientKubePathMap")
    public HashMap<String,ConcurrentHashMap<String,ClusterFlushEntity>> caffeineCache() {
        return new HashMap<>();
    }

    @Bean("blockingQueue")
    public LinkedBlockingDeque<ClusterFlushEntity> createBlockingQueue(@Qualifier("watcherConfig") WatcherConfig watcherConfig){
        return new LinkedBlockingDeque<>(watcherConfig.getConsumeQueueCapacity());
    }


    @Bean("cacheKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

}
