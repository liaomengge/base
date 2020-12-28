package com.github.liaomengge.base_common.dayu.custom.consts;

/**
 * Created by liaomengge on 17/1/10.
 */
public interface CircuitBreakerConst {

    interface MetricConst {
        String CIRCUIT_BREAKER_PREFIX = "metric-circuit-breaker.";
    }

    interface CacheKeySuffixConst {
        String REDIS_LATEST_FAILURE_TIME = ":latest:failure:time";
    }
}
