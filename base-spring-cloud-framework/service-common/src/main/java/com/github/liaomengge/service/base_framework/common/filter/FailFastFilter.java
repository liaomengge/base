package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.base.code.SystemResultCode;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2018/11/29.
 */
@Order(500)
@AllArgsConstructor
public class FailFastFilter extends AbstractFilter {

    private final FilterConfig filterConfig;

    @Override
    public Object doFilter(MethodInvocation invocation, FilterChain chain) throws Throwable {
        String failFastMethodName = filterConfig.getFailFast().getMethodName();
        if (StringUtils.isBlank(failFastMethodName)) {
            return chain.doFilter(invocation, chain);
        }
        String methodName = super.getMethodName(invocation);
        Iterable<String> iterable = SPLITTER.split(failFastMethodName);
        if (!Iterables.contains(iterable, methodName)) {
            return chain.doFilter(invocation, chain);
        }
        return DataResult.fail(SystemResultCode.FAIL_FAST_ERROR);
    }
}
