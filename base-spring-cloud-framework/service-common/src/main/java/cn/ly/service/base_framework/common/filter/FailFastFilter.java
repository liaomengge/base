package cn.ly.service.base_framework.common.filter;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.base.code.SystemResultCode;
import cn.ly.service.base_framework.common.config.FilterConfig;
import cn.ly.service.base_framework.common.filter.chain.FilterChain;

import com.google.common.collect.Iterables;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

import lombok.AllArgsConstructor;

/**
 * Created by liaomengge on 2018/11/29.
 */
@Order(500)
@AllArgsConstructor
public class FailFastFilter extends AbstractFilter {

    private final FilterConfig filterConfig;

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        String failFastMethodName = filterConfig.getFailFast().getMethodName();
        if (StringUtils.isBlank(failFastMethodName)) {
            return chain.doFilter(joinPoint, chain);
        }
        String methodName = super.getMethodName(joinPoint);
        Iterable<String> iterable = SPLITTER.split(failFastMethodName);
        if (!Iterables.contains(iterable, methodName)) {
            return chain.doFilter(joinPoint, chain);
        }
        return DataResult.fail(SystemResultCode.FAIL_FAST_ERROR);
    }
}
