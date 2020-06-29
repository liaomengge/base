package cn.mwee.base_common.helper.cache;

import cn.mwee.base_common.helper.redis.IRedisHelper;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import cn.mwee.base_common.utils.string.MwStringUtil;
import cn.mwee.base_common.utils.thread.MwThreadPoolExecutorUtil;
import com.alibaba.fastjson.TypeReference;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 16/12/12.
 */
@Deprecated
public class RedisDbCacheUtil {

    private static final int FIVE_MIN = 300;//为避免缓存穿透,默认缓存5分钟
    private static final String SYNCHRONIZED_PREFIX = "synchronized:";

    private ThreadPoolExecutor cacheThreadPool = MwThreadPoolExecutorUtil.buildCpuCoreThreadPool("cache",
            30L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(32));

    @Setter
    private CachePool cachePool;

    @Setter
    private IRedisHelper jedisClusterHelper;

    /**
     * 本地缓存(L1) + Redis缓存(L2)
     *
     * @param key
     * @param iRedisDbCache
     * @return
     */
    public String invoke(String key, IRedisDbCache<String> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                return iRedisDbCache.handle();
            }

            cachePool.put(key, value);
            return value;
        }

        return value;
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param iRedisDbCache
     * @return
     */
    public String invokeSync(String key, IRedisDbCache<String> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                synchronized (getSynchronizedKey(key)) {
                    value = jedisClusterHelper.get(key);
                    if (StringUtils.isBlank(value)) {
                        return iRedisDbCache.handle();
                    }
                }
            }

            cachePool.put(key, value);
            return value;
        }

        return value;
    }

    /**
     * Redis缓存(L2)
     *
     * @param key
     * @param iRedisDbCache
     * @return
     */
    public String invoke2(String key, IRedisDbCache<String> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return iRedisDbCache.handle();
        }

        return value;
    }

    /**
     * Redis缓存(L2) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param iRedisDbCache
     * @return
     */
    public String invokeSync2(String key, IRedisDbCache<String> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return iRedisDbCache.handle();
                }
            }
        }

        return value;
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     * @param key
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, TypeReference<T> typeReference, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return iRedisDbCache.handle();
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, Class<T> clazz, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return iRedisDbCache.handle();
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGenericSync2(String key, TypeReference<T> typeReference, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return iRedisDbCache.handle();
                }
            }
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGenericSync2(String key, Class<T> clazz, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return iRedisDbCache.handle();
                }
            }
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                                IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = iRedisDbCache.handle();
            if (t == null) {
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, boolean isSaveNull, int redisExpiresInSeconds,
                                TypeReference<T> typeReference, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = iRedisDbCache.handle();
            if (t == null) {
                if (isSaveNull) {
                    jedisClusterHelper.set(key, "", FIVE_MIN);
                }
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, Class<T> clazz,
                                IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = iRedisDbCache.handle();
            if (t == null) {
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, boolean isSaveNull, int redisExpiresInSeconds,
                                Class<T> clazz, IRedisDbCache<T> iRedisDbCache) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = iRedisDbCache.handle();
            if (t == null) {
                if (isSaveNull) {
                    jedisClusterHelper.set(key, "", FIVE_MIN);
                }
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                               IRedisDbCache<T> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = iRedisDbCache.handle();
                if (t == null) {
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, typeReference);
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, boolean isSaveNull, int redisExpiresInSeconds,
                               TypeReference<T> typeReference, IRedisDbCache<T> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = iRedisDbCache.handle();
                if (t == null) {
                    if (isSaveNull) {
                        jedisClusterHelper.set(key, "", FIVE_MIN);
                    }
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, typeReference);
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, int redisExpiresInSeconds, Class<T> clazz,
                               IRedisDbCache<T> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = iRedisDbCache.handle();
                if (t == null) {
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, clazz);
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param iRedisDbCache
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, boolean isSaveNull, int redisExpiresInSeconds,
                               Class<T> clazz, IRedisDbCache<T> iRedisDbCache) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = iRedisDbCache.handle();
                if (t == null) {
                    if (isSaveNull) {
                        jedisClusterHelper.set(key, "", FIVE_MIN);
                    }
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, clazz);
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /***************************************
     * 华丽的分割线(Jdk8)
     *****************************************/

    /**
     * 本地缓存(L1) + Redis缓存(L2)
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invoke(String key, Supplier<String> supplier) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                return supplier.get();
            }

            cachePool.put(key, value);
            return value;
        }

        return value;
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invokeSync(String key, Supplier<String> supplier) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                synchronized (getSynchronizedKey(key)) {
                    value = jedisClusterHelper.get(key);
                    if (StringUtils.isBlank(value)) {
                        return supplier.get();
                    }
                }
            }

            cachePool.put(key, value);
            return value;
        }

        return value;
    }

    /**
     * Redis缓存(L2)
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invoke2(String key, Supplier<String> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return supplier.get();
        }

        return value;
    }

    /**
     * Redis缓存(L2) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param supplier
     * @return
     */
    public String invokeSync2(String key, Supplier<String> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return supplier.get();
                }
            }
        }

        return value;
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return supplier.get();
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, Class<T> clazz, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            return supplier.get();
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericSync2(String key, TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return supplier.get();
                }
            }
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic) + synchronized
     * 注：iRedisDbCache#handle必须实现数据回写到redis, 不然效果更差
     *
     * @param key
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGenericSync2(String key, Class<T> clazz, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            synchronized (getSynchronizedKey(key)) {
                value = jedisClusterHelper.get(key);
                if (StringUtils.isBlank(value)) {
                    return supplier.get();
                }
            }
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, TypeReference<T> typeReference,
                                Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = supplier.get();
            if (t == null) {
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, boolean isSaveNull, int redisExpiresInSeconds,
                                TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = supplier.get();
            if (t == null) {
                if (isSaveNull) {
                    jedisClusterHelper.set(key, "", FIVE_MIN);
                }
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, int redisExpiresInSeconds, Class<T> clazz, Supplier<T>
            supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = supplier.get();
            if (t == null) {
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric2(String key, boolean isSaveNull, int redisExpiresInSeconds,
                                Class<T> clazz, Supplier<T> supplier) {
        String value = jedisClusterHelper.get(key);
        if (StringUtils.isBlank(value)) {
            T t = supplier.get();
            if (t == null) {
                if (isSaveNull) {
                    jedisClusterHelper.set(key, "", FIVE_MIN);
                }
                return null;
            }

            if (redisExpiresInSeconds > 0) {
                String tJson = MwJsonUtil.toJson(t);
                jedisClusterHelper.set(key, tJson, redisExpiresInSeconds);
            }
            return t;
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
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
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = supplier.get();
                if (t == null) {
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, typeReference);
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param typeReference
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, boolean isSaveNull, int redisExpiresInSeconds,
                               TypeReference<T> typeReference, Supplier<T> supplier) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = supplier.get();
                if (t == null) {
                    if (isSaveNull) {
                        jedisClusterHelper.set(key, "", FIVE_MIN);
                    }
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, typeReference);
        }

        return MwJsonUtil.fromJson(value, typeReference);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, int redisExpiresInSeconds, Class<T> clazz,
                               Supplier<T> supplier) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = supplier.get();
                if (t == null) {
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, clazz);
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    /**
     * 本地缓存(L1) + Redis缓存(L2) + 泛型化(Generic)
     *
     * @param key
     * @param isSaveNull
     * @param redisExpiresInSeconds
     * @param clazz
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T invokeGeneric(String key, boolean isSaveNull, int redisExpiresInSeconds,
                               Class<T> clazz, Supplier<T> supplier) {
        String value = MwStringUtil.getValue(cachePool.get(key));
        if (StringUtils.isBlank(value)) {
            value = jedisClusterHelper.get(key);
            if (StringUtils.isBlank(value)) {
                T t = supplier.get();
                if (t == null) {
                    if (isSaveNull) {
                        jedisClusterHelper.set(key, "", FIVE_MIN);
                    }
                    return null;
                }

                String tJson = MwJsonUtil.toJson(t);
                cachePool.put(key, tJson);
                if (redisExpiresInSeconds > 0) {
                    cacheThreadPool.execute(() -> jedisClusterHelper.set(key, tJson, redisExpiresInSeconds));
                }
                return t;
            }

            cachePool.put(key, value);
            return MwJsonUtil.fromJson(value, clazz);
        }

        return MwJsonUtil.fromJson(value, clazz);
    }

    private String getSynchronizedKey(String key) {
        return SYNCHRONIZED_PREFIX + key;
    }
}
