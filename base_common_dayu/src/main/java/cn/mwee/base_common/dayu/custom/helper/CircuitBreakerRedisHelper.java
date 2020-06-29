package cn.mwee.base_common.dayu.custom.helper;

import cn.mwee.base_common.dayu.custom.config.CircuitBreakerConfig;
import cn.mwee.base_common.dayu.custom.consts.CircuitBreakerConst;
import cn.mwee.base_common.dayu.custom.lua.LuaUtil;
import cn.mwee.base_common.helper.redis.IRedisHelper;
import cn.mwee.base_common.utils.number.MwMoreNumberUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static cn.mwee.base_common.dayu.custom.lua.LuaUtil.CIRCUIT_COUNTER;

/**
 * Created by liaomengge on 2019/10/30.
 */
@AllArgsConstructor
public class CircuitBreakerRedisHelper {

    @Getter
    private final IRedisHelper iRedisHelper;
    @Getter
    private final CircuitBreakerConfig circuitBreakerConfig;

    public String getLatestFailureTimeStr(String resource) {
        return "{" + resource + "}" + CircuitBreakerConst.CacheKeySuffix.REDIS_LATEST_FAILURE_TIME;
    }

    public long getLatestFailureTime(String resource) {
        return MwMoreNumberUtil.toLong(iRedisHelper.get(this.getLatestFailureTimeStr(resource)));
    }

    public int getFailureCount(String resource) {
        String failureCountStr = iRedisHelper.get(resource);
        return MwMoreNumberUtil.toInt(failureCountStr);
    }

    public void incrFailureCount(String resource) {
        iRedisHelper.eval(LuaUtil.getLuaMap().get(CIRCUIT_COUNTER), Lists.newArrayList(resource),
                Lists.newArrayList(String.valueOf(circuitBreakerConfig.getFailureIntervalSeconds())));
    }
}
