package com.github.liaomengge.base_common.dayu.custom.helper;

import com.github.liaomengge.base_common.dayu.custom.config.CircuitBreakerConfig;
import com.github.liaomengge.base_common.dayu.custom.consts.CircuitBreakerConst;
import com.github.liaomengge.base_common.dayu.custom.lua.LuaUtil;
import com.github.liaomengge.base_common.helper.redis.IRedisHelper;
import com.github.liaomengge.base_common.utils.number.LyMoreNumberUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by liaomengge on 2019/10/30.
 */
@AllArgsConstructor
public class CircuitBreakerRedisHelper {

    @Getter
    private final IRedisHelper iRedisHelper;
    @Getter
    private final CircuitBreakerConfig circuitBreakerConfig;

    public String getLatestFailureTimeKey(String resource) {
        return "{" + resource + "}" + CircuitBreakerConst.CacheKeySuffixConst.REDIS_LATEST_FAILURE_TIME;
    }

    public long getLatestFailureTime(String resource) {
        return LyMoreNumberUtil.toLong(iRedisHelper.get(this.getLatestFailureTimeKey(resource)));
    }

    public int getFailureCount(String resource) {
        return LyMoreNumberUtil.toInt(iRedisHelper.get(resource));
    }

    public void incrFailureCount(String resource) {
        iRedisHelper.eval(LuaUtil.getLuaMap().get(LuaUtil.CIRCUIT_COUNTER), Lists.newArrayList(resource),
                Lists.newArrayList(String.valueOf(circuitBreakerConfig.getFailureIntervalSeconds())));
    }
}
