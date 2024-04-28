package com.dpline.console.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * custom cache key generator
 */
public class CacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(@Nonnull Object target,
                           @Nonnull Method method,
                           @Nullable Object... params) {
        return StringUtils.arrayToDelimitedString(params, "_");
    }
}
