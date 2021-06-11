package com.github.liaomengge.base_common.design.patterns.chain.filter2;

import lombok.Getter;

import java.util.Objects;

/**
 * Created by liaomengge on 2021/6/11.
 */
public abstract class Filter<T, R> {

    @Getter
    private Filter<T, R> nextFilter;

    public R doFilter(T t) throws Throwable {
        if (isOwn(t)) {
            return this.apply(t);
        }
        Filter<T, R> nextFilter = this.getNextFilter();
        if (Objects.nonNull(nextFilter)) {
            return nextFilter.doFilter(t);
        }
        return null;
    }

    protected abstract R apply(T t) throws Throwable;

    protected abstract boolean isOwn(T t);

    protected Filter<T, R> setNextFilter(Filter<T, R> nextFilter) {
        this.nextFilter = nextFilter;
        return nextFilter;
    }

}
