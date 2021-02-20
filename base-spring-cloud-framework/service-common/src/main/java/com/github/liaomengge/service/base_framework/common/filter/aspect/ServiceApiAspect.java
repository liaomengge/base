package com.github.liaomengge.service.base_framework.common.filter.aspect;

import com.github.liaomengge.base_common.support.datasource.DBContext;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.log.LyMDCUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.github.liaomengge.base_common.utils.web.LyWebAopUtil;
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
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2018/10/23.
 */
public class ServiceApiAspect implements MethodInterceptor, Ordered {

    private static final Logger log = LyLogger.getInstance(ServiceApiAspect.class);

    @Getter
    private FilterChain defaultFilterChain;

    @Setter
    private FilterChain filterChain;

    @Setter
    private FilterConfig filterConfig = new FilterConfig();

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TimeThreadLocalUtil.set(System.nanoTime());
        ServiceApiLogInfo requestApiLogInfo = buildRequestLog(invocation);

        FilterChain filterChain = null;
        try {
            filterChain = defaultFilterChain.cloneChain();
            Object retObj = filterChain.doFilter(invocation, filterChain);
            buildResponseLog(invocation, retObj, requestApiLogInfo);
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

    private ServiceApiLogInfo buildClassName(MethodInvocation invocation) {
        ServiceApiLogInfo apiLogInfo = new ServiceApiLogInfo();
        apiLogInfo.setClassMethod('[' + ServiceApiLogUtil.getClassName(invocation) + '#' + ServiceApiLogUtil.getMethodName(invocation) + ']');
        return apiLogInfo;
    }

    private void buildHeaderLog(MethodInvocation invocation, ServiceApiLogInfo apiLogInfo) {
        if (ServiceApiLogUtil.isIgnoreLogHeader(invocation, filterConfig) || ServiceApiLogUtil.isIgnoreAopLogHeader(invocation)) {
            return;
        }
        LyWebUtil.getHttpServletRequest().ifPresent(val -> {
            Map<String, String> headerMap = LyWebUtil.getRequestStringHeaders(val);
            apiLogInfo.setHeaderParams(headerMap);
        });
    }

    private ServiceApiLogInfo buildRequestLog(MethodInvocation invocation) {
        ServiceApiLogInfo apiLogInfo = buildClassName(invocation);
        buildHeaderLog(invocation, apiLogInfo);
        buildArgsLog(invocation, apiLogInfo);
        LyWebUtil.getHttpServletRequest().ifPresent(request -> {
            apiLogInfo.setHttpMethod(request.getMethod());
            apiLogInfo.setQueryParams(request.getQueryString());
            LyMDCUtil.put(LyMDCUtil.MDC_API_REMOTE_IP, LyNetworkUtil.getRemoteIpAddress(request));
            LyMDCUtil.put(LyMDCUtil.MDC_API_URI, request.getRequestURI());
        });
        return apiLogInfo;
    }

    private void buildArgsLog(MethodInvocation invocation, ServiceApiLogInfo apiLogInfo) {
        if (ServiceApiLogUtil.isIgnoreLogRequest(invocation, filterConfig) || ServiceApiLogUtil.isIgnoreAopLogRequest(invocation)) {
            return;
        }
        Object requestParams = LyWebAopUtil.getRequestParams(ServiceApiLogUtil.getMethod(invocation),
                invocation.getArguments());
        apiLogInfo.setRequestBody(requestParams);
    }

    private void buildResponseLog(MethodInvocation invocation, Object retObj, ServiceApiLogInfo apiLogInfo) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        if (!ServiceApiLogUtil.isIgnoreLogResponse(invocation, filterConfig) && !ServiceApiLogUtil.isIgnoreAopLogResponse(invocation)) {
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
                    .addFilter(new TraceFilter())
                    .addFilter(new FailFastFilter(filterConfig))
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
