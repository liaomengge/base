package cn.ly.base_common.cache.caffeine;

import cn.ly.base_common.cache.Level1Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.Getter;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/3/18.
 */
public class CaffeineCache implements Level1Cache {

    private final boolean allowNullValues;
    @Getter
    private final com.github.benmanes.caffeine.cache.Cache<String, String> cache;

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<String, String> cache) {
        this(cache, true);
    }

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<String, String> cache, boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
        this.cache = cache;
    }

    @Override
    public String get(String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void set(String key, String value) {
        if (Objects.isNull(value)) {
            if (this.allowNullValues) {
                this.cache.put(key, value);
            }
            return;
        }
        this.cache.put(key, value);
    }

    @Override
    public void evict(String key) {
        this.cache.invalidate(key);
    }

    @Override
    public long size() {
        return this.cache.estimatedSize();
    }

    @Override
    public void evictAll() {
        this.cache.invalidateAll();
    }

    @Override
    public CacheStats stats() {
        return this.cache.stats();
    }
}
