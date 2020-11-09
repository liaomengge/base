package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.service.base_framework.common.filter.chain.ServiceApiFilter;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by liaomengge on 2018/11/21.
 */
public abstract class AbstractFilter implements ServiceApiFilter {

    protected String getMethodName(MethodInvocation invocation) {
        return invocation.getMethod().getName();
    }
}
