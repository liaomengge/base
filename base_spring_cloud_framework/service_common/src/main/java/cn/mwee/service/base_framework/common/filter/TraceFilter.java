package cn.mwee.service.base_framework.common.filter;

import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import cn.mwee.service.base_framework.common.filter.chain.FilterChain;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static cn.mwee.base_common.utils.trace.MwTraceLogUtil.*;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(0)
public class TraceFilter extends AbstractFilter{

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String traceId = StringUtils.defaultIfBlank(request.getHeader(TRACE_ID),
                generateRandomSed(generateDefaultTraceLogIdPrefix()));
        MwTraceLogUtil.put(traceId);
        return chain.doFilter(joinPoint, chain);
    }
}
