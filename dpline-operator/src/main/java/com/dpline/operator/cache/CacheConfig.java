package com.dpline.operator.cache;

import com.dpline.operator.job.ClusterFlushEntity;
import com.dpline.operator.config.WatcherConfig;
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
    public HashMap<Long,ConcurrentHashMap<String, ClusterFlushEntity>> caffeineCache() {
        return new HashMap<>();
    }

    @Bean("cacheKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

}
