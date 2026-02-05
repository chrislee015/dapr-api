package com.app.sandboxtwo.services.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static com.app.sandboxtwo.configuration.CacheConfiguration.*;

@Slf4j
@Component
public class LowPriorityCacheStrategy implements CacheStrategy {

    private static final int ORDER = 3;
    private final AtomicLong hitCounter = new AtomicLong(0);
    private final AtomicLong missCounter = new AtomicLong(0);

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
        return "LOW_PRIORITY_CACHE";
    }

    @Override
    public void logCacheActivity(String operation, String key, boolean hit, Object result) {
        if (hit) {
            long hits = hitCounter.incrementAndGet();
            log.info("[{}] Cache HIT #{} - Key: {}, Operation: {}, Hit Ratio: {:.2f}%", 
                    getStrategyName(), hits, key, operation, getHitRatio());
        } else {
            long misses = missCounter.incrementAndGet();
            log.info("[{}] Cache MISS #{} - Key: {}, Operation: {}, Miss Ratio: {:.2f}%", 
                    getStrategyName(), misses, key, operation, getMissRatio());
        }
    }

    @Cacheable(cacheNames = POSTS_CACHE, key = "'bulk_posts_' + #batchId", 
               condition = "#batchId != null")
    public Object getCachedBulkPosts(String batchId, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Fetching bulk posts for batch: {}", getStrategyName(), batchId);
        Object result = dataSupplier.get();
        logCacheActivity("BULK_POSTS", "bulk_posts_" + batchId, false, result);
        return result;
    }

    @Cacheable(cacheNames = METADATA_CACHE, key = "'stats_' + #type", unless = "#result == null")
    public Object getCachedStatistics(String type, java.util.function.Supplier<Object> dataSupplier) {
        log.info("[{}] Fetching statistics for type: {}", getStrategyName(), type);
        Object result = dataSupplier.get();
        logCacheActivity("STATISTICS", "stats_" + type, false, result);
        return result;
    }

    @CacheEvict(cacheNames = {POSTS_CACHE, METADATA_CACHE}, 
                key = "'bulk_posts_*'", condition = "#clearBulk == true")
    public void evictBulkCaches(boolean clearBulk) {
        log.info("[{}] Evicting bulk caches, clearBulk: {}", getStrategyName(), clearBulk);
        logCacheActivity("EVICT_BULK", "bulk_posts_*", false, null);
    }

    public void logCacheStatistics() {
        long hits = hitCounter.get();
        long misses = missCounter.get();
        long total = hits + misses;
        
        log.info("[{}] Cache Statistics - Hits: {}, Misses: {}, Total: {}, Hit Ratio: {:.2f}%", 
                getStrategyName(), hits, misses, total, getHitRatio());
    }

    private double getHitRatio() {
        long total = hitCounter.get() + missCounter.get();
        return total == 0 ? 0.0 : (hitCounter.get() * 100.0) / total;
    }

    private double getMissRatio() {
        long total = hitCounter.get() + missCounter.get();
        return total == 0 ? 0.0 : (missCounter.get() * 100.0) / total;
    }
}
