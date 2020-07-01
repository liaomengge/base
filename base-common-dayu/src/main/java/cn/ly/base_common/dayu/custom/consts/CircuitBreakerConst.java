package cn.ly.base_common.dayu.custom.consts;

/**
 * Created by liaomengge on 17/1/10.
 */
public interface CircuitBreakerConst {

    interface Metric {
        String CIRCUIT_BREAKER_PREFIX = "metric-circuit-breaker.";
    }

    /**
     * 缓存后缀
     */
    interface CacheKeySuffix {
        String REDIS_LATEST_FAILURE_TIME = ":latest:failure:time";
    }
}
