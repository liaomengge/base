package com.github.liaomengge.base_common.cache;

import com.github.liaomengge.base_common.cache.caffeine.CaffeineCache;
import com.github.liaomengge.base_common.cache.caffeine.CaffeineCacheManager;
import com.github.liaomengge.base_common.cache.channel.Channel;
import com.github.liaomengge.base_common.cache.consts.CacheConst;
import com.github.liaomengge.base_common.cache.domain.CacheDomain;
import com.github.liaomengge.base_common.cache.enums.NotifyTypeEnum;
import com.github.liaomengge.base_common.cache.redis.RedisCache;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2019/3/19.
 */
@Slf4j
public class CachePoolHelper {

    private final CaffeineCacheManager caffeineCacheManager;

    private final RedisCache redisCache;

    private final Channel channel;

    public CachePoolHelper(CaffeineCacheManager caffeineCacheManager, RedisCache redisCache, Channel channel) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCache = redisCache;
        this.channel = channel;
    }

    public Map<String, CaffeineCache> getLevel1CacheMap() {
        Map<String, CaffeineCache> caffeineCacheMap = Maps.newHashMap();
        caffeineCacheManager.getCacheRegions().stream().forEach(val -> {
            caffeineCacheMap.put(val, caffeineCacheManager.getCache(val));
        });
        return caffeineCacheMap;
    }

    public CaffeineCache getLevel1Cache() {
        return getLevel1Cache(caffeineCacheManager.getDefaultRegion());
    }

    public CaffeineCache getLevel1Cache(String region) {
        return caffeineCacheManager.getCache(region);
    }

    public RedisCache getRedisCache() {
        return redisCache;
    }

    protected String getLockKey(String lockKey) {
        return CacheConst.LOCK_PREFIX + lockKey;
    }

    public String getFromLevel1(String key) {
        return getFromLevel1(caffeineCacheManager.getDefaultRegion(), key);
    }

    public String getFromLevel1(String region, String key) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return null;
        }
        return caffeineCacheManager.getCache(region).get(key);
    }

    public <T> T getFromLevel1(String key, Class<T> clz) {
        return getFromLevel1(caffeineCacheManager.getDefaultRegion(), key, clz);
    }

    public <T> T getFromLevel1(String region, String key, Class<T> clz) {
        String level1Json = getFromLevel1(region, key);
        if (StringUtils.isBlank(level1Json)) {
            return null;
        }
        return LyJacksonUtil.fromJson(level1Json, clz);
    }

    public String getFromLevel2(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return redisCache.get(key);
    }

    public <T> T getFromLevel2(String key, Class<T> clz) {
        String level2Json = getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            return null;
        }
        return LyJacksonUtil.fromJson(level2Json, clz);
    }

    public String get(String key) {
        return get(caffeineCacheManager.getDefaultRegion(), key);
    }

    public String get(String region, String key) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return null;
        }
        String level1Json = getFromLevel1(region, key);
        if (StringUtils.isBlank(level1Json)) {
            String lockKey = getLockKey(region + ':' + key);
            synchronized (lockKey) {
                level1Json = getFromLevel1(region, key);
                if (StringUtils.isNotBlank(level1Json)) {
                    return level1Json;
                }
                try {
                    String level2Json = getFromLevel2(key);
                    if (StringUtils.isNotBlank(level2Json)) {
                        CacheDomain cacheDomain =
                                CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.PUT).region(region).key(key).value(level2Json).build();
                        sendPubCmd(cacheDomain);
                    }
                    return level2Json;
                } catch (Exception e) {
                    log.error("获取一二级缓存region[" + region + "],key[" + key + "]失败", e);
                }
            }

        }
        return level1Json;
    }

    public <T> T get(String key, Class<T> clz) {
        return get(caffeineCacheManager.getDefaultRegion(), key, clz);
    }

    public <T> T get(String region, String key, Class<T> clz) {
        String json = get(region, key);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return LyJacksonUtil.fromJson(json, clz);
    }

    public void setToLevel1(String region, String key, String value) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        caffeineCacheManager.getCache(region).set(key, value);
    }

    public <T> void setToLevel1(String region, String key, T value) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        caffeineCacheManager.getCache(region).set(key, LyJacksonUtil.toJson(value));
    }

    public void setToLevel2(String key, String value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisCache.set(key, value);
    }

    public <T> void setToLevel2(String key, T value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisCache.set(key, LyJacksonUtil.toJson(value));
    }

    public void setToLevel2(String key, String value, int expiredSeconds) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisCache.set(key, value, expiredSeconds);
    }

    public <T> void setToLevel2(String key, T value, int expiredSeconds) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisCache.set(key, LyJacksonUtil.toJson(value), expiredSeconds);
    }

    public void set(String key, String value) {
        set(caffeineCacheManager.getDefaultRegion(), key, value);
    }

    public void set(String region, String key, String value) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        try {
            redisCache.set(key, value);
            CacheDomain cacheDomain =
                    CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.PUT).region(region).key(key).value(value).build();
            sendPubCmd(cacheDomain);
        } catch (Exception e) {
            log.error("设置一二级缓存region[" + region + "],key[" + key + "]失败", e);
        }
    }

    public <T> void set(String key, T value) {
        set(caffeineCacheManager.getDefaultRegion(), key, value);
    }

    public <T> void set(String region, String key, T value) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        try {
            String json = LyJacksonUtil.toJson(value);
            redisCache.set(key, json);
            CacheDomain cacheDomain =
                    CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.PUT).region(region).key(key).value(json).build();
            sendPubCmd(cacheDomain);
        } catch (Exception e) {
            log.error("设置一二级缓存region[" + region + "],key[" + key + "]失败", e);
        }
    }

    public void set(String key, String value, int level2ExpiredSeconds) {
        set(caffeineCacheManager.getDefaultRegion(), key, value, level2ExpiredSeconds);
    }

    public void set(String region, String key, String value, int level2ExpiredSeconds) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        try {
            redisCache.set(key, value, level2ExpiredSeconds);
            CacheDomain cacheDomain =
                    CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.PUT).region(region).key(key).value(value).build();
            sendPubCmd(cacheDomain);
        } catch (Exception e) {
            log.error("设置一二级缓存region[" + region + "],key[" + key + "]失败", e);
        }
    }

    public <T> void set(String key, T value, int level2ExpiredSeconds) {
        set(caffeineCacheManager.getDefaultRegion(), key, value, level2ExpiredSeconds);
    }

    public <T> void set(String region, String key, T value, int level2ExpiredSeconds) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        try {
            String json = LyJacksonUtil.toJson(value);
            redisCache.set(key, json, level2ExpiredSeconds);
            CacheDomain cacheDomain =
                    CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.PUT).region(region).key(key).value(json).build();
            sendPubCmd(cacheDomain);
        } catch (Exception e) {
            log.error("设置一二级缓存region[" + region + "],key[" + key + "]失败", e);
        }
    }

    public void evictLevel1(String key) {
        evictLevel1(caffeineCacheManager.getDefaultRegion(), key);
    }

    public void evictLevel1(String region, String key) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        caffeineCacheManager.getCache(region).evict(key);
    }

    public void evictLevel2(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisCache.evict(key);
    }

    public void evict(String key) {
        evict(caffeineCacheManager.getDefaultRegion(), key);
    }

    public void evict(String region, String key) {
        if (StringUtils.isBlank(region) || StringUtils.isBlank(key)) {
            return;
        }
        try {
            redisCache.evict(key);
            CacheDomain cacheDomain =
                    CacheDomain.builder().notifyTypeEnum(NotifyTypeEnum.DEL).region(region).key(key).build();
            sendPubCmd(cacheDomain);
        } catch (Exception e) {
            log.error("删除一二级缓存region[" + region + "],key[" + key + "]失败", e);
        }
    }

    public long level1Size() {
        return level1Size(caffeineCacheManager.getDefaultRegion());
    }

    public long level1Size(String region) {
        return caffeineCacheManager.getCache(region).size();
    }

    private void sendPubCmd(CacheDomain cacheDomain) {
        channel.doPubChannel(LyJsonUtil.toJson(cacheDomain));
    }

    @PostConstruct
    private void init() {
        channel.doSubChannel(msg -> {
            CacheDomain cacheDomain = LyJacksonUtil.fromJson(msg, CacheDomain.class);
            if (Objects.isNull(cacheDomain)) {
                return;
            }
            log.info("[Topic]同步一级缓存信息 ===> {}", msg);
            NotifyTypeEnum notifyTypeEnum = cacheDomain.getNotifyTypeEnum();
            switch (notifyTypeEnum) {
                case PUT:
                    log.info("[Topic]设置一级缓存key[{}],value[{}]", cacheDomain.getKey(), cacheDomain.getValue());
                    caffeineCacheManager.getCache(cacheDomain.getRegion()).set(cacheDomain.getKey(),
                            cacheDomain.getValue());
                    break;
                case DEL:
                    log.info("[Topic]删除一级缓存key[{}]", cacheDomain.getKey());
                    caffeineCacheManager.getCache(cacheDomain.getRegion()).evict(cacheDomain.getKey());
                    break;
                default:
                    break;
            }
        });
    }
}
