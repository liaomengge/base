package cn.ly.base_common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/3/19.
 */
public class CaffeineCacheManager {

    private final ConcurrentMap<String, CaffeineCache> caffeineCacheMap = new ConcurrentHashMap<>(16);

    private Caffeine<Object, Object> caffeineBuilder =
            Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS).maximumSize(256).recordStats();
    private ConcurrentMap<String, Caffeine<Object, Object>> caffeineBuilderMap = new ConcurrentHashMap<>(16);

    private boolean dynamic = true;
    private boolean allowNullValues = true;

    @Setter
    @Getter
    private String defaultRegion = "caffeine";

    public CaffeineCacheManager() {
    }

    public CaffeineCacheManager(String... cacheRegions) {
        setCacheNames(Arrays.asList(cacheRegions));
    }

    public void setCacheNames(Collection<String> cacheRegions) {
        if (cacheRegions != null) {
            for (String region : cacheRegions) {
                this.caffeineBuilderMap.put(region, caffeineBuilder);
                this.caffeineCacheMap.put(region, createCaffeineCache(region));
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }

    public void setCaffeine(String region, Caffeine<Object, Object> caffeine) {
        Assert.notNull(caffeine, "Caffeine must not be null");
        doSetCaffeine(region, caffeine);
    }

    public void setCaffeineSpec(String region, CaffeineSpec caffeineSpec) {
        doSetCaffeine(region, Caffeine.from(caffeineSpec));
    }

    public void setCacheSpecification(String region, String cacheSpecification) {
        doSetCaffeine(region, Caffeine.from(cacheSpecification));
    }

    public void setCaffeineSpec(Map<String, CaffeineSpec> caffeineSpecMap) {
        Map<String, Caffeine<Object, Object>> cacheBuilder = Maps.newHashMap();
        caffeineSpecMap.forEach((key, val) -> cacheBuilder.put(key, Caffeine.from(val)));
        doSetCaffeineMap(cacheBuilder);
    }

    public void setAllowNullValues(boolean allowNullValues) {
        if (this.allowNullValues != allowNullValues) {
            this.allowNullValues = allowNullValues;
        }
    }

    public boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    public Collection<String> getCacheRegions() {
        return Collections.unmodifiableSet(this.caffeineCacheMap.keySet());
    }

    public CaffeineCache getCache(String region) {
        CaffeineCache caffeineCache = this.caffeineCacheMap.get(region);
        if (caffeineCache == null && this.dynamic) {
            synchronized (this.caffeineCacheMap) {
                caffeineCache = this.caffeineCacheMap.get(region);
                if (caffeineCache == null) {
                    caffeineCache = createCaffeineCache(region);
                    this.caffeineCacheMap.put(region, caffeineCache);
                }
            }
        }
        return caffeineCache;
    }

    protected CaffeineCache createCaffeineCache(String region) {
        return new CaffeineCache(createNativeCaffeineCache(region), isAllowNullValues());
    }

    protected com.github.benmanes.caffeine.cache.Cache<String, String> createNativeCaffeineCache(String region) {
        Caffeine<Object, Object> caffeine = this.caffeineBuilderMap.get(region);
        if (Objects.isNull(caffeine)) {
            return caffeineBuilder.build();
        }
        return caffeine.build();
    }

    private void doSetCaffeine(String region, Caffeine<Object, Object> cacheBuilder) {
        this.caffeineBuilderMap.put(region, cacheBuilder);
        refreshKnownCache(region);
    }

    private void doSetCaffeineMap(Map<String, Caffeine<Object, Object>> cacheBuilder) {
        this.caffeineBuilderMap.putAll(cacheBuilder);
        cacheBuilder.keySet().stream().forEach(this::refreshKnownCache);
    }

    private void refreshKnownCache(String region) {
        this.caffeineCacheMap.put(region, createCaffeineCache(region));
    }
}
