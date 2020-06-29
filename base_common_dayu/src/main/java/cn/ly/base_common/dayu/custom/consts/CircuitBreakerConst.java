package cn.ly.base_common.dayu.custom.consts;

/**
 * Created by liaomengge on 17/1/10.
 */
public class CircuitBreakerConst {

    public static class Metric {
        public static final String CIRCUIT_BREAKER_PREFIX = "metric-circuit-breaker.";
    }

    /**
     * 缓存后缀
     */
    public static class CacheKeySuffix {
        public static final String REDIS_LATEST_FAILURE_TIME = ":latest:failure:time";
    }
}
