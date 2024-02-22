package com.handsome.console.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean("caffeineCache")
    public Caffeine<Object, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 24小时自动过期
                .expireAfterWrite(24, TimeUnit.HOURS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(100000);
    }

    @Bean("caffeineCacheManager")
    public CacheManager cacheManager(@Qualifier("caffeineCache") Caffeine<Object, Object>  caffeineCache) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCache);
        return cacheManager;
    }

    @Bean("cacheKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

}
