package com.dpline.k8s.operator.cache;

import com.dpline.k8s.operator.job.ClusterFlushEntity;
import com.dpline.operator.common.WatcherConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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
