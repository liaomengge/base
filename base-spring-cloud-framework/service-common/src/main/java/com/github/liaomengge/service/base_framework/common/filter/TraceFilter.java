package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.base_common.utils.mdc.LyMDCUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import static com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil.*;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(0)
public class TraceFilter extends AbstractFilter {

    @Override
    public Object doFilter(MethodInvocation invocation, FilterChain chain) throws Throwable {
        LyWebUtil.getHttpServletRequest().ifPresent(request -> {
            String traceId = StringUtils.defaultIfBlank(request.getHeader(TRACE_ID),
                    generateRandomSed(generateDefaultTraceLogIdPrefix()));
            LyMDCUtil.put(LyMDCUtil.MDC_TRACE_ID, traceId);
        });
        return chain.doFilter(invocation, chain);
    }
}
