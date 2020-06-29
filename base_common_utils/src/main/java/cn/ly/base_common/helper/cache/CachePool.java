package cn.ly.base_common.helper.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by liaomengge on 16/7/25.
 */
@Deprecated
public class CachePool {

    private final String defaultCacheName;
    private final CacheManager cacheManager;

    public CachePool(String defaultCacheName, CacheManager cacheManager) {
        this.defaultCacheName = defaultCacheName;
        this.cacheManager = cacheManager;
    }

    public Cache<Object, Object> getCache() {
        return getCache(defaultCacheName);
    }

    public Cache<Object, Object> getCache(String cacheName) {
        return ((CaffeineCache) cacheManager.getCache(cacheName)).getNativeCache();
    }

    public Object get(Object key) {
        return getCache(defaultCacheName).getIfPresent(key);
    }

    public Object get(String cacheName, Object key) {
        Object obj = getCache(cacheName).getIfPresent(key);
        if (obj == null || StringUtils.isBlank(obj.toString())) {
            return null;
        }
        return obj;
    }

    public Object get(Object key, Function<? super Object, ? extends Object> function) {
        return getCache(defaultCacheName).get(key, function);
    }

    public Object get(String cacheName, Object key, Function<? super Object, ? extends Object> function) {
        return getCache(cacheName).get(key, function);
    }

    public void put(Object key, Object value) {
        put(defaultCacheName, key, value);
    }

    /**
     * 注意: 此处不支持缓存数据为null
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, Object key, Object value) {
        if (value == null) {
            return;
        }
        getCache(cacheName).put(key, value);
    }

    public void putAll(Map<Object, Object> map) {
        getCache(defaultCacheName).putAll(map);
    }

    public void putAll(String cacheName, Map<Object, Object> map) {
        getCache(cacheName).putAll(map);
    }

    public void remove(Object key) {
        getCache(defaultCacheName).invalidate(key);
    }

    public void remove(String cacheName, Object key) {
        getCache(cacheName).invalidate(key);
    }

    public void removeAll() {
        getCache(defaultCacheName).invalidateAll();
    }

    public void removeAll(String cacheName) {
        getCache(cacheName).invalidateAll();
    }

    public long size() {
        return getCache(defaultCacheName).estimatedSize();
    }

    public long size(String cacheName) {
        return getCache(cacheName).estimatedSize();
    }

}
