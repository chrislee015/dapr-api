package com.app.sandboxtwo.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String POSTS_CACHE = "posts";
    public static final String CLIENTS_CACHE = "clients";
    public static final String HEALTH_CACHE = "health";
    public static final String METADATA_CACHE = "metadata";

    @Bean
    @Primary
    public CacheManager cacheManager() {
        log.info("Initializing ConcurrentMapCacheManager with caches: {}, {}, {}, {}",
                POSTS_CACHE, CLIENTS_CACHE, HEALTH_CACHE, METADATA_CACHE);

        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                POSTS_CACHE, CLIENTS_CACHE, HEALTH_CACHE, METADATA_CACHE);

        cacheManager.setAllowNullValues(false);

        log.info("Cache manager initialized successfully");
        return cacheManager;
    }

    @CacheEvict(allEntries = true, cacheNames = {POSTS_CACHE, CLIENTS_CACHE, HEALTH_CACHE, METADATA_CACHE})
    public void evictAllCaches() {
        log.info("Evicting all caches");
    }
}
