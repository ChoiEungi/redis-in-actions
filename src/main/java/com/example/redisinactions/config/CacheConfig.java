package com.example.redisinactions.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig implements CachingConfigurer {

    private final CacheManager redisCacheManager;

    @Override
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return redisCacheManager;
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

}
