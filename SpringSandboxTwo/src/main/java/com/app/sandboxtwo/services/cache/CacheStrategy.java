package com.app.sandboxtwo.services.cache;

import org.springframework.core.Ordered;

public interface CacheStrategy extends Ordered, Comparable<Ordered> {
    String getStrategyName();
    void logCacheActivity(String operation, String key, boolean hit, Object result);
    default void preCache(String key, Object data) {
        logCacheActivity("PRE_CACHE", key, false, data);
    }
    default void postCache(String key, Object data) {
        logCacheActivity("POST_CACHE", key, false, data);
    }
}
