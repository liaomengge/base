package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil.*;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(0)
public class TraceFilter extends AbstractFilter {

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String traceId = StringUtils.defaultIfBlank(request.getHeader(TRACE_ID),
                    generateRandomSed(generateDefaultTraceLogIdPrefix()));
            LyTraceLogUtil.put(traceId);
        }
        return chain.doFilter(joinPoint, chain);
    }
}
