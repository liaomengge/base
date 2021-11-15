package com.github.liaomengge.base_common.design.patterns.chain.filter;

/**
 * Created by liaomengge on 2021/6/10.
 */
public interface FilterChain<T, R> {

    R filter(T t) throws Throwable;

    default R doFilter(T t) throws Throwable {
        return null;
    }
}
