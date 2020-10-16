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
import org.springframework.core.io.InputStreamSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
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
        StringBuilder reqArgsBuilder = buildRequestLog(joinPoint);

        FilterChain filterChain = null;
        try {
            filterChain = defaultFilterChain.cloneChain();
            Object retObj = filterChain.doFilter(joinPoint, filterChain);
            buildResultLog(joinPoint, retObj, reqArgsBuilder);
            return retObj;
        } catch (Exception e) {
            buildExceptionResultLog(e, reqArgsBuilder);
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

    private StringBuilder buildRequestLog(ProceedingJoinPoint joinPoint) {
        StringBuilder sBuilder = new StringBuilder();
        buildHeaderLog(sBuilder);
        if (ServiceApiLogUtil.isIgnoreLogArgsMethod(joinPoint, filterConfig) || ServiceApiLogUtil.isIgnoreAopLogArgsMethod(joinPoint)) {
            return sBuilder;
        }
        Method method = ServiceApiLogUtil.getMethod(joinPoint);
        sBuilder.append(", method => " + method.getName());
        sBuilder.append(", args => ");
        Object[] args = joinPoint.getArgs();
        buildArgsLog(args, sBuilder);
        LyWebUtil.getHttpServletRequest().ifPresent(val -> {
            LyMDCUtil.put(LyMDCUtil.MDC_API_REMOTE_IP, LyNetworkUtil.getIpAddress(val));
            LyMDCUtil.put(LyMDCUtil.MDC_API_URI, val.getRequestURI());
        });
        return sBuilder;
    }

    private void buildHeaderLog(StringBuilder sBuilder) {
        LyWebUtil.getHttpServletRequest().ifPresent(val -> {
            Map<String, String> headerMap = LyWebUtil.getRequestHeaders(val);
            sBuilder.append("header => " + LyJsonUtil.toJson4Log(headerMap));
        });
    }

    private void buildArgsLog(Object[] args, StringBuilder sBuilder) {
        if (Objects.isNull(args) || args.length <= 0) {
            sBuilder.append("null,");
            return;
        }
        Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(val -> !(val instanceof HttpServletResponse || val instanceof MultipartFile
                        || val instanceof InputStream || val instanceof InputStreamSource || val instanceof BindingResult))
                .forEach(val -> {
                    if (val instanceof HttpServletRequest) {
                        sBuilder.append(LyJsonUtil.toJson4Log(LyWebUtil.getRequestParams((HttpServletRequest) val))).append(',');
                    } else if (val instanceof WebRequest) {
                        sBuilder.append(LyJsonUtil.toJson4Log(((WebRequest) val).getParameterMap())).append(',');
                    } else {
                        sBuilder.append(LyJsonUtil.toJson4Log(val)).append(',');
                    }
                });
    }

    private void buildResultLog(ProceedingJoinPoint joinPoint, Object retObj, StringBuilder sBuilder) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        if (!ServiceApiLogUtil.isIgnoreLogResultMethod(joinPoint, filterConfig) && !ServiceApiLogUtil.isIgnoreAopLogResultMethod(joinPoint)) {
            if (retObj instanceof DataResult) {
                DataResult dataResult = (DataResult) retObj;
                dataResult.setElapsedMilliSeconds(elapsedNanoTime);
                sBuilder.append(" result => " + LyJsonUtil.toJson4Log(dataResult));
            } else if (retObj instanceof String) {
                sBuilder.append(" result => " + retObj);
            } else {
                sBuilder.append(" result => " + LyJsonUtil.toJson4Log(retObj));
            }
        }
        LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));
        log.info("请求响应日志: {}", sBuilder.toString());
    }

    private void buildExceptionResultLog(Exception e, StringBuilder sBuilder) {
        long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
        sBuilder.append(" exception result => " + LyThrowableUtil.getStackTrace(e));
        LyMDCUtil.put(LyMDCUtil.MDC_API_ELAPSED_MILLI_TIME,
                String.valueOf(TimeUnit.NANOSECONDS.toMillis(elapsedNanoTime)));
        log.error("请求响应日志: {}", sBuilder.toString());
    }

    @PostConstruct
    private void init() {
        defaultFilterChain = new FilterChain();
        boolean enabledDefaultFilter = filterConfig.isEnabledDefaultFilter();
        if (enabledDefaultFilter) {
            defaultFilterChain.addFilter(new FailFastFilter(filterConfig))
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
