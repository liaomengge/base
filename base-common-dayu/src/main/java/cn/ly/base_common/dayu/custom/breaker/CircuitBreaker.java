package cn.ly.base_common.dayu.custom.breaker;

/**
 * Created by liaomengge on 2019/6/26.
 */
public interface CircuitBreaker<R> {

    R execute();

    R fallback();
}
