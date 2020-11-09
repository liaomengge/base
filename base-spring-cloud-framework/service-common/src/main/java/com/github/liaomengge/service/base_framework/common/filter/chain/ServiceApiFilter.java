package com.github.liaomengge.service.base_framework.common.filter.chain;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by liaomengge on 2018/11/19.
 */
public interface ServiceApiFilter extends IOrdered {

    Object doFilter(MethodInvocation invocation, FilterChain chain) throws Throwable;
}
