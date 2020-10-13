package com.github.liaomengge.base_common.dayu.sentinel.circuit;

/**
 * Created by liaomengge on 2019/10/30.
 */
public interface SentinelCircuitBreaker<R> {

    R execute();

    R block();

    R fallback();
}
