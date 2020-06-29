package cn.mwee.base_common.cache.redis;

import cn.mwee.base_common.cache.Level2Cache;
import cn.mwee.base_common.helper.redis.RedissonHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 2019/3/18.
 */
@AllArgsConstructor
public class RedisCache implements Level2Cache {

    private final boolean allowNullValues;

    private final RedissonHelper redissonHelper;

    @Override
    public String get(String key) {
        return redissonHelper.get(key);
    }

    @Override
    public void set(String key, String value) {
        if (StringUtils.isBlank(value)) {
            if (this.allowNullValues) {
                this.redissonHelper.set(key, value);
            }
            return;
        }
        this.redissonHelper.set(key, value);
    }

    @Override
    public void set(String key, String value, int expiredSeconds) {
        if (StringUtils.isBlank(value)) {
            if (this.allowNullValues) {
                this.redissonHelper.set(key, value, expiredSeconds);
            }
            return;
        }
        this.redissonHelper.set(key, value, expiredSeconds);
    }

    @Override
    public void evict(String key) {
        this.redissonHelper.del(key);
    }
}
