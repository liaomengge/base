package com.github.liaomengge.service.base_framework.common.filter.aspect;

import com.github.liaomengge.base_common.support.datasource.DBContext;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.base_common.utils.web.LyWebUtil;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.*;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.github.liaomengge.service.base_framework.common.pojo.ServiceApiLogInfo;
import com.github.liaomengge.service.base_framework.common.util.ServiceApiLogUtil;
import com.github.liaomengge.service.base_framework.common.util.TimeThreadLocalUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2018/10/23.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceApiAspect {

    private static final Logger log = LyLogger.getInstance(ServiceApiAspect.class);

    @Getter
    private FilterChain defaultFilterChain;

    @Setter
    private FilterChain filterChain;

    @Setter
    private FilterConfig filterConfig = new FilterConfig();

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Around("target(com.github.liaomengge.service.base_framework.api.BaseFrameworkServiceApi) " +
            "&& execution(public * *(..)) ")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        TimeThreadLocalUtil.set(System.nanoTime());
        ServiceApiLogInfo requestApiLogInfo = buildRequestLog(joinPoint);

        FilterChain filterChain = null;
        try {
            filterChain = defaultFilterChain.cloneChain();
            Object retObj = filterChain.doFilter(joinPoint, filterChain);
            buildResponseLog(joinPoint, retObj, requestApiLogInfo);
            return retObj;
        } catch (Exception e) {
            buildExceptionResponseLog(e, requestApiLogInfo);
            throw e;
        } finally {
            Optional.ofNullable(filterChain).ifPresent(FilterChain::reset);

            TimeThreadLocalUtil.remove();

            DBContext.clearDBKey();

            LyTraceLogUtil.clearTrace();

            LyMDCUtil.remove(LyMDCUtil.MDC_API_REMOTE_IP);
            LyMDCUtil.remove(LyMDCUtil.MDC_API_URI);
            LyMDCUtil.remove(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME);
        }
    }

    private ServiceApiLogInfo buildClassName(ProceedingJoinPoint joinPoint) {
        ServiceApiLogInfo apiLogInfo = new ServiceApiLogInfo();
        apiLogInfo.setClassMethod('[' + ServiceApiLogUtil.getClassName(joinPoint) + '#' + ServiceApiLogUtil.getMethodName(joinPoint) + ']');
        return apiLogInfo;
    }

    private void buildHeaderLog(ProceedingJoinPoint joinPoint, ServiceApiLogInfo apiLogInfo) {
        if (ServiceApiLogUtil.isIgnoreLogHeader(joinPoint, filterConfig) || ServiceApiLogUtil.isIgnoreAopLogHeader(joinPoint)) {
            return;
        }
        LyWebUtil.getHttpServletRequest().ifPresent(val -> {
            Map<String, String> headerMap = LyWebUtil.getRequestHeaders(val);
            apiLogInfo.setHeaderParams(headerMap);
        });
    }

    private ServiceApiLogInfo buildRequestLog(ProceedingJoinPoint joinPoint) {
        ServiceApiLogInfo apiLogInfo = buildClassName(joinPoint);
        buildHeaderLog(joinPoint, apiLogInfo);
        buildArgsLog(joinPoint, apiLogInfo);
        LyWebUtil.getHttpServletRequest().ifPresent(val -> {
            apiLogInfo.setHttpMethod(val.getMethod());
            apiLogInfo.setQueryParams(val.getQueryString());
            LyMDCUtil.put(LyMDCUtil.MDC_API_REMOTE_IP, LyNetworkUtil.getIpAddress(val));
            LyMDCUtil.put(LyMDCUtil.MDC_API_URI, val.getRequestURI());
        });
        return apiLogInfo;
    }

    private void buildArgsLog(ProceedingJoinPoint joinPoint, ServiceApiLogInfo apiLogInfo) {
        if (ServiceApiLogUtil.isIgnoreLogRequest(joinPoint, filterConfig) || ServiceApiLogUtil.isIgnoreAopLogRequest(joinPoint)) {
            return;
        }
        Object requestParams = LyWebUtil.getRequestParams(ServiceApiLogUtil.getMethod(joinPoint), joinPoint.getArgs());
        apiLogInfo.setRequestBody(requestParams);
    }

    private void buildResponseLog(ProceedingJoinPoint joinPoint, Object retObj, ServiceApiLogInfo apiLogInfo) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        if (!ServiceApiLogUtil.isIgnoreLogResponse(joinPoint, filterConfig) && !ServiceApiLogUtil.isIgnoreAopLogResponse(joinPoint)) {
            if (retObj instanceof DataResult) {
                DataResult dataResult = (DataResult) retObj;
                dataResult.setElapsedMilliSeconds(elapsedNanoTime);
                apiLogInfo.setResponseBody(dataResult);
            } else {
                apiLogInfo.setResponseBody(retObj);
            }
        }
        LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));
        log.info("request response log info => {}", LyJsonUtil.toJson4Log(apiLogInfo));
    }

    private void buildExceptionResponseLog(Exception e, ServiceApiLogInfo apiLogInfo) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        apiLogInfo.setExceptionStackTrace(LyThrowableUtil.getStackTrace(e));
        LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));
        log.error("request response log info => {}", LyJsonUtil.toJson4Log(apiLogInfo));
    }

    @PostConstruct
    private void init() {
        defaultFilterChain = new FilterChain();
        boolean enabledDefaultFilter = filterConfig.isEnabledDefaultFilter();
        if (enabledDefaultFilter) {
            defaultFilterChain
                    .addFilter(new FailFastFilter(filterConfig))
                    .addFilter(new TraceFilter())
                    .addFilter(new SignFilter(filterConfig))
                    .addFilter(new ParamValidateFilter())
                    .addFilter(new MetricsFilter(meterRegistry));
        }
        if (Objects.nonNull(filterChain)) {
            defaultFilterChain.addFilter(filterChain.getFilters());
        }
        defaultFilterChain.sortFilters();
        LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME, NumberUtils.INTEGER_ZERO.toString());
        log.info("sort filter chain ===> {}", defaultFilterChain.printFilters());
    }
}
