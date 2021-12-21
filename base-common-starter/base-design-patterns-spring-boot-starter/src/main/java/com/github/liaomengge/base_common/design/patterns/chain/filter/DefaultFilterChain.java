package com.github.liaomengge.base_common.design.patterns.chain.filter;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.OrderUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2021/6/10.
 */
public class DefaultFilterChain<T, R> implements FilterChain<T, R> {

    public int pos = 0;
    public List<Filter<T, R>> filters = Lists.newArrayList();

    public DefaultFilterChain() {
    }

    public DefaultFilterChain(List<Filter<T, R>> filters) {
        this.pos = 0;
        this.filters = filters;
    }

    public DefaultFilterChain(int pos, List<Filter<T, R>> filters) {
        this.pos = pos;
        this.filters = filters;
    }

    public FilterChain<T, R> cloneChain() {
        return new DefaultFilterChain(this.pos, this.filters);
    }

    public boolean hasNextFilter() {
        return pos < filters.size();
    }

    public FilterChain<T, R> addFilter(Filter<T, R> filter) {
        if (Objects.isNull(filters)) {
            filters = Lists.newArrayList();
        }
        filters.add(filter);
        return this;
    }

    public FilterChain<T, R> addFilter(List<Filter<T, R>> filterList) {
        if (Objects.isNull(filters)) {
            filters = Lists.newArrayList();
        }
        filters.addAll(filterList);
        return this;
    }

    public void sortFilters() {
        filters = filters.stream()
                .sorted(Comparator.comparingInt(filter -> LyNumberUtil.getIntValue(OrderUtils.getOrder(filter.getClass(), filter.getOrder()))))
                .collect(Collectors.toList());
    }

    public String printFilters() {
        return filters.parallelStream()
                .map(filter -> {
                    String filterName = filter.getClass().getSimpleName();
                    int order = LyNumberUtil.getIntValue(OrderUtils.getOrder(filter.getClass(), filter.getOrder()));
                    return filterName + "(" + order + ")";
                }).reduce((val, val2) -> val + ',' + val2).orElse("null");
    }

    public void reset() {
        pos = 0;
        filters = null;
    }

    @Override
    public R filter(T t) throws Throwable {
        if (hasNextFilter()) {
            Filter<T, R> filter = filters.get(pos++);
            if (filter.skip(t)) {
                return this.filter(t);
            }
            return filter.doFilter(t, this);
        }
        return this.doFilter(t);
    }

}
