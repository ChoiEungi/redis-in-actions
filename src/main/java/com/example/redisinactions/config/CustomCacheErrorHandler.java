package com.example.redisinactions.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.data.redis.serializer.SerializationException;

@Slf4j
public class CustomCacheErrorHandler extends SimpleCacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        if (exception instanceof SerializationException) {
            log.warn("Failed to deserialize cache value for key: {}", key, exception);
            return;
        }

        super.handleCacheGetError(exception, cache, key);
    }

}
