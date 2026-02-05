package com.app.sandboxtwo.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.app.sandboxtwo.configuration.CacheConfiguration.*;

@Slf4j
@Component
public class MediumPriorityCacheStrategy implements CacheStrategy {

    private static final int ORDER = 2;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compareTo(Ordered o) {
        return Integer.compare(ORDER, o.getOrder());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public String getStrategyName() {
        return "MEDIUM_PRIORITY_CACHE";
    }

    @Override
    public void logCacheActivity(String operation, String key, boolean hit, Object result) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        if (hit) {
            log.info("[{}] [{}] Cache HIT - Key: {}, Operation: {}, Data Size: {}", 
                    getStrategyName(), timestamp, key, operation, getDataSize(result));
        } else {
            log.info("[{}] [{}] Cache MISS - Key: {}, Operation: {}, Loading fresh data", 
                    getStrategyName(), timestamp, key, operation);
        }
    }

    @Cacheable(cacheNames = HEALTH_CACHE, key = "'health_' + #endpoint", 
               condition = "#endpoint != null")
    public Object getCachedHealthCheck(String endpoint, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Performing health check for endpoint: {}", getStrategyName(), endpoint);
        Object result = dataSupplier.get();
        logCacheActivity("HEALTH_CHECK", "health_" + endpoint, false, result);
        return result;
    }

    @Cacheable(cacheNames = METADATA_CACHE, key = "'meta_' + #category + '_' + #identifier")
    public Object getCachedMetadata(String category, String identifier, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Fetching metadata for category: {}, identifier: {}", 
                getStrategyName(), category, identifier);
        Object result = dataSupplier.get();
        logCacheActivity("FETCH_METADATA", "meta_" + category + "_" + identifier, false, result);
        return result;
    }

    @CacheEvict(cacheNames = HEALTH_CACHE, allEntries = true)
    public void evictAllHealthCache() {
        log.info("[{}] Evicting all health check caches", getStrategyName());
        logCacheActivity("EVICT_ALL_HEALTH", "health_*", false, null);
    }

    private String getDataSize(Object data) {
        if (data == null) return "0";
        if (data instanceof java.util.Collection<?> collection) {
            return String.valueOf(collection.size());
        }
        if (data instanceof java.util.Map<?, ?> map) {
            return String.valueOf(map.size());
        }
        return "1";
    }
}
