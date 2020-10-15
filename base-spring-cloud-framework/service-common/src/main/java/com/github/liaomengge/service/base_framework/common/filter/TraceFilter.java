package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

import static com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil.*;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(0)
public class TraceFilter extends AbstractFilter {

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        LyWebUtil.getHttpServletRequest().ifPresent(request -> {
            String traceId = StringUtils.defaultIfBlank(request.getHeader(TRACE_ID),
                    generateRandomSed(generateDefaultTraceLogIdPrefix()));
            LyTraceLogUtil.put(traceId);
        });
        return chain.doFilter(joinPoint, chain);
    }
}
