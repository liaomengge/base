package com.github.liaomengge.base_common.design.patterns.chain.filter2;

import lombok.Getter;
import org.springframework.core.Ordered;

import java.util.Objects;

/**
 * Created by liaomengge on 2021/6/11.
 */
public abstract class Filter<T, R> implements Ordered {

    @Getter
    private Filter<T, R> nextFilter;

    public R filter(T t) throws Throwable {
        R result = null;
        if (!skip(t)) {
            result = this.doFilter(t);
        }
        Filter<T, R> nextFilter = this.getNextFilter();
        if (Objects.nonNull(nextFilter)) {
            return nextFilter.filter(t);
        }
        return result;
    }

    protected abstract R doFilter(T t) throws Throwable;

    protected Filter<T, R> setNextFilter(Filter<T, R> nextFilter) {
        this.nextFilter = nextFilter;
        return nextFilter;
    }

    protected boolean skip(T t) {
        return false;
    }

    protected String getFilterName() {
        return this.getClass().getTypeName();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
