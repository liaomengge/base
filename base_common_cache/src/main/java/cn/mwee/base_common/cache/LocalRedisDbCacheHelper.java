package cn.mwee.base_common.cache;

import cn.mwee.base_common.cache.consts.CacheConst;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

/**
 * Created by liaomengge on 2019/7/2.
 */
public class LocalRedisDbCacheHelper {

    private final CachePoolHelper cachePoolHelper;

    public LocalRedisDbCacheHelper(CachePoolHelper cachePoolHelper) {
        this.cachePoolHelper = cachePoolHelper;
    }

    /**
     * L1 + L2 + DB
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invoke(String key, Supplier<String> supplier) {
        String json = cachePoolHelper.get(key);
        if (StringUtils.isBlank(json)) {
            synchronized (getSynchronizedKey(key)) {
                json = cachePoolHelper.get(key);
                if (StringUtils.isNotBlank(json)) {
                    return json;
                }
                json = supplier.get();
                cachePoolHelper.set(key, json);
                return json;
            }
        }
        return json;
    }

    /**
     * L1 + L2 + DB + Generic
     *
     * @param key
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, Class<T> clazz, Supplier<T> supplier) {
        String json = cachePoolHelper.get(key);
        if (StringUtils.isBlank(json)) {
            synchronized (getSynchronizedKey(key)) {
                json = cachePoolHelper.get(key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, clazz);
                }
                T t = supplier.get();
                cachePoolHelper.set(key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, clazz);
    }

    /**
     * L1 + L2 + DB + Generic
     *
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String json = cachePoolHelper.get(key);
        if (StringUtils.isBlank(json)) {
            synchronized (getSynchronizedKey(key)) {
                json = cachePoolHelper.get(key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, typeReference);
                }
                T t = supplier.get();
                cachePoolHelper.set(key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, typeReference);
    }

    /**
     * L1 + L2 + DB + Generic + ttl
     * 注意：如果此时L1的过期时间 > redisExpiresInSeconds,L2失效后, L1仍然有效
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, int redisExpiresInSeconds, Class<T> clazz, Supplier<T> supplier) {
        String json = cachePoolHelper.get(key);
        if (StringUtils.isBlank(json)) {
            String lockKey = key;
            synchronized (getSynchronizedKey(key)) {
                json = cachePoolHelper.get(key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, clazz);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.set(key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, clazz);
    }

    /**
     * L1 + L2 + DB + Generic + ttl
     * 注意：如果此时L1的过期时间 > redisExpiresInSeconds,L2失效后, L1仍然有效
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                               Supplier<T> supplier) {
        String json = cachePoolHelper.get(key);
        if (StringUtils.isBlank(json)) {
            synchronized (getSynchronizedKey(key)) {
                json = cachePoolHelper.get(key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, typeReference);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.set(key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, typeReference);
    }

    /*************************************************华丽的分割线***********************************************/

    /**
     * L1 + L2 + DB
     *
     * @param region
     * @param key
     * @param supplier
     * @return
     */
    public String invoke(String region, String key, Supplier<String> supplier) {
        String json = cachePoolHelper.get(region, key);
        if (StringUtils.isBlank(json)) {
            String synchronizedKey = region + ':' + key;
            synchronized (getSynchronizedKey(synchronizedKey)) {
                json = cachePoolHelper.get(region, key);
                if (StringUtils.isNotBlank(json)) {
                    return json;
                }
                json = supplier.get();
                cachePoolHelper.set(region, key, json);
                return json;
            }
        }
        return json;
    }

    /**
     * L1 + L2 + DB + Generic
     *
     * @param region
     * @param key
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String region, String key, Class<T> clazz, Supplier<T> supplier) {
        String json = cachePoolHelper.get(region, key);
        if (StringUtils.isBlank(json)) {
            String synchronizedKey = region + ':' + key;
            synchronized (getSynchronizedKey(synchronizedKey)) {
                json = cachePoolHelper.get(region, key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, clazz);
                }
                T t = supplier.get();
                cachePoolHelper.set(region, key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, clazz);
    }

    /**
     * L1 + L2 + DB + Generic
     *
     * @param region
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String region, String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String json = cachePoolHelper.get(region, key);
        if (StringUtils.isBlank(json)) {
            String synchronizedKey = region + ':' + key;
            synchronized (getSynchronizedKey(synchronizedKey)) {
                json = cachePoolHelper.get(region, key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, typeReference);
                }
                T t = supplier.get();
                cachePoolHelper.set(region, key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, typeReference);
    }

    /**
     * L1 + L2 + DB + Generic + ttl
     * 注意：如果此时L1的过期时间 > redisExpiresInSeconds,L2失效后, L1仍然有效
     *
     * @param region
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String region, String key, int redisExpiresInSeconds, Class<T> clazz,
                               Supplier<T> supplier) {
        String json = cachePoolHelper.get(region, key);
        if (StringUtils.isBlank(json)) {
            String synchronizedKey = region + ':' + key;
            synchronized (getSynchronizedKey(synchronizedKey)) {
                json = cachePoolHelper.get(region, key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, clazz);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.set(region, key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, clazz);
    }

    /**
     * L1 + L2 + DB + Generic + ttl
     * 注意：如果此时L1的过期时间 > redisExpiresInSeconds,L2失效后, L1仍然有效
     *
     * @param region
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String region, String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                               Supplier<T> supplier) {
        String json = cachePoolHelper.get(region, key);
        if (StringUtils.isBlank(json)) {
            String synchronizedKey = region + ':' + key;
            synchronized (getSynchronizedKey(synchronizedKey)) {
                json = cachePoolHelper.get(region, key);
                if (StringUtils.isNotBlank(json)) {
                    return MwJsonUtil.fromJson(json, typeReference);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.set(region, key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(json, typeReference);
    }

    /*************************************************华丽的分割线***********************************************/

    /**
     * L2 + DB
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invokeFromLevel2(String key, Supplier<String> supplier) {
        String level2Json = cachePoolHelper.getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            synchronized (getSynchronizedKey(key)) {
                level2Json = cachePoolHelper.getFromLevel2(key);
                if (StringUtils.isNotBlank(level2Json)) {
                    return level2Json;
                }
                level2Json = supplier.get();
                cachePoolHelper.setToLevel2(key, level2Json);
                return level2Json;
            }
        }
        return level2Json;
    }

    /**
     * L2 + DB + Generic
     *
     * @param key
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericFromLevel2(String key, Class<T> clazz, Supplier<T> supplier) {
        String level2Json = cachePoolHelper.getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            synchronized (getSynchronizedKey(key)) {
                level2Json = cachePoolHelper.getFromLevel2(key);
                if (StringUtils.isNotBlank(level2Json)) {
                    return MwJsonUtil.fromJson(level2Json, clazz);
                }
                T t = supplier.get();
                cachePoolHelper.setToLevel2(key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(level2Json, clazz);
    }

    /**
     * L2 + DB + Generic
     *
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericFromLevel2(String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String level2Json = cachePoolHelper.getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            synchronized (getSynchronizedKey(key)) {
                level2Json = cachePoolHelper.getFromLevel2(key);
                if (StringUtils.isNotBlank(level2Json)) {
                    return MwJsonUtil.fromJson(level2Json, typeReference);
                }
                T t = supplier.get();
                cachePoolHelper.setToLevel2(key, t);
                return t;
            }
        }
        return MwJsonUtil.fromJson(level2Json, typeReference);
    }

    /**
     * L2 + DB + Generic + ttl
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericFromLevel2(String key, int redisExpiresInSeconds, Class<T> clazz, Supplier<T> supplier) {
        String level2Json = cachePoolHelper.getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            synchronized (getSynchronizedKey(key)) {
                level2Json = cachePoolHelper.getFromLevel2(key);
                if (StringUtils.isNotBlank(level2Json)) {
                    return MwJsonUtil.fromJson(level2Json, clazz);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.setToLevel2(key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(level2Json, clazz);
    }

    /**
     * L2 + DB + Generic + ttl
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericFromLevel2(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                                         Supplier<T> supplier) {
        String level2Json = cachePoolHelper.getFromLevel2(key);
        if (StringUtils.isBlank(level2Json)) {
            synchronized (getSynchronizedKey(key)) {
                level2Json = cachePoolHelper.getFromLevel2(key);
                if (StringUtils.isNotBlank(level2Json)) {
                    return MwJsonUtil.fromJson(level2Json, typeReference);
                }
                T t = supplier.get();
                if (redisExpiresInSeconds > 0) {
                    cachePoolHelper.setToLevel2(key, t, redisExpiresInSeconds);
                }
                return t;
            }
        }
        return MwJsonUtil.fromJson(level2Json, typeReference);
    }

    private String getSynchronizedKey(String key) {
        return CacheConst.SYNCHRONIZED_PREFIX + key;
    }
}
