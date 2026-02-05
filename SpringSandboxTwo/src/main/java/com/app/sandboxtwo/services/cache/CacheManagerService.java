package com.app.sandboxtwo.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@Service
public class CacheManagerService {

    private final List<CacheStrategy> cacheStrategies;
    private final CacheManager cacheManager;
    private final HighPriorityCacheStrategy highPriorityCache;
    private final MediumPriorityCacheStrategy mediumPriorityCache;
    private final LowPriorityCacheStrategy lowPriorityCache;

    @Autowired
    public CacheManagerService(List<CacheStrategy> cacheStrategies, 
                              CacheManager cacheManager,
                              HighPriorityCacheStrategy highPriorityCache,
                              MediumPriorityCacheStrategy mediumPriorityCache,
                              LowPriorityCacheStrategy lowPriorityCache) {
        this.cacheStrategies = cacheStrategies.stream()
                .sorted()
                .toList();
        this.cacheManager = cacheManager;
        this.highPriorityCache = highPriorityCache;
        this.mediumPriorityCache = mediumPriorityCache;
        this.lowPriorityCache = lowPriorityCache;
        
        logInitializedStrategies();
    }

    public Object getCachedPosts(String clientId, Supplier<Object> dataSupplier) {
        log.debug("Routing posts request for client: {} through high priority cache", clientId);
        return highPriorityCache.getCachedPosts(clientId, dataSupplier);
    }

    public Object getCachedClient(String clientId, Supplier<Object> dataSupplier) {
        log.debug("Routing client request for: {} through high priority cache", clientId);
        return highPriorityCache.getCachedClient(clientId, dataSupplier);
    }

    public Object getCachedHealthCheck(String endpoint, Supplier<Object> dataSupplier) {
        log.debug("Routing health check for: {} through medium priority cache", endpoint);
        return mediumPriorityCache.getCachedHealthCheck(endpoint, dataSupplier);
    }

    public Object getCachedBulkPosts(String batchId, Supplier<Object> dataSupplier) {
        log.debug("Routing bulk posts request for batch: {} through low priority cache", batchId);
        return lowPriorityCache.getCachedBulkPosts(batchId, dataSupplier);
    }

    public void evictClientCaches(String clientId) {
        log.info("Evicting all caches for client: {}", clientId);
        highPriorityCache.evictClientCaches(clientId);
    }

    public void evictAllHealthCaches() {
        log.info("Evicting all health check caches");
        mediumPriorityCache.evictAllHealthCache();
    }

    public void printCacheStatistics() {
        log.info("=== CACHE STATISTICS SUMMARY ===");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                log.info("Cache [{}]: Active", cacheName);
            }
        });
        
        if (lowPriorityCache instanceof LowPriorityCacheStrategy lowPriority) {
            lowPriority.logCacheStatistics();
        }
        
        log.info("=== END CACHE STATISTICS ===");
    }

    private void logInitializedStrategies() {
        log.info("Initialized {} cache strategies in order:", cacheStrategies.size());
        cacheStrategies.forEach(strategy -> 
            log.info("  Order {}: {}", strategy.getOrder(), strategy.getStrategyName())
        );
    }
}
