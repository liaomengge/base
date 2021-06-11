package com.github.liaomengge.base_common.design.patterns.chain.filter;

import org.springframework.core.Ordered;

/**
 * Created by liaomengge on 2021/6/10.
 */
public interface Filter<T, R> extends Ordered {

    R doFilter(T t, FilterChain<T, R> filterChain) throws Throwable;

    default boolean skip(T t) {
        return false;
    }

    default void init() {
    }

    default void deploy() {
    }

    default String getFilterName() {
        return this.getClass().getTypeName();
    }

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
