package com.github.liaomengge.service.base_framework.common.filter.chain;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by liaomengge on 2018/11/19.
 */
public interface ServiceApiFilter extends IOrdered {

    Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable;
}
