package com.app.sandboxtwo.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static com.app.sandboxtwo.configuration.CacheConfiguration.*;

@Slf4j
@Component
public class HighPriorityCacheStrategy implements CacheStrategy {

    private static final int ORDER = 1;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public String getStrategyName() {
        return "HIGH_PRIORITY_CACHE";
    }

    @Override
    public void logCacheActivity(String operation, String key, boolean hit, Object result) {
        if (hit) {
            log.info("[{}] Cache HIT - Key: {}, Operation: {}, Result: {}", 
                    getStrategyName(), key, operation, 
                    result != null ? result.getClass().getSimpleName() : "null");
        } else {
            log.info("[{}] Cache MISS - Key: {}, Operation: {}, Computing new value", 
                    getStrategyName(), key, operation);
        }
    }

    @Cacheable(cacheNames = POSTS_CACHE, key = "'posts_' + #clientId", 
               condition = "#clientId != null")
    public Object getCachedPosts(String clientId, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Fetching posts for client: {}", getStrategyName(), clientId);
        Object result = dataSupplier.get();
        logCacheActivity("FETCH_POSTS", "posts_" + clientId, false, result);
        return result;
    }

    @Cacheable(cacheNames = CLIENTS_CACHE, key = "'client_' + #clientId", 
               condition = "#clientId != null")
    public Object getCachedClient(String clientId, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Fetching client data for: {}", getStrategyName(), clientId);
        Object result = dataSupplier.get();
        logCacheActivity("FETCH_CLIENT", "client_" + clientId, false, result);
        return result;
    }

    @CachePut(cacheNames = CLIENTS_CACHE, key = "'client_' + #clientId")
    public Object updateClientCache(String clientId, Object clientData) {
        log.info("[{}] Updating client cache for: {}", getStrategyName(), clientId);
        logCacheActivity("UPDATE_CLIENT", "client_" + clientId, false, clientData);
        return clientData;
    }

    @CacheEvict(cacheNames = {POSTS_CACHE, CLIENTS_CACHE}, key = "'*_' + #clientId")
    public void evictClientCaches(String clientId) {
        log.info("[{}] Evicting all caches for client: {}", getStrategyName(), clientId);
        logCacheActivity("EVICT_CLIENT", "*_" + clientId, false, null);
    }

    @Override
    public int compareTo(Ordered o) {
        return Integer.compare(ORDER, o.getOrder());
    }
}
