package com.github.liaomengge.base_common.dayu.custom.consts;

/**
 * Created by liaomengge on 17/1/10.
 */
public interface CircuitBreakerConst {

    interface Metric {
        String CIRCUIT_BREAKER_PREFIX = "metric-circuit-breaker.";
    }

    interface CacheKeySuffix {
        String REDIS_LATEST_FAILURE_TIME = ":latest:failure:time";
    }
}
