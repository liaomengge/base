package cn.ly.service.base_framework.common.filter.aspect;

import cn.ly.base_common.support.datasource.DBContext;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.log.LyMDCUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.net.LyNetworkUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import cn.ly.base_common.utils.web.LyWebUtil;
import cn.ly.service.base_framework.base.DataResult;
import cn.ly.service.base_framework.common.config.FilterConfig;
import cn.ly.service.base_framework.common.filter.*;
import cn.ly.service.base_framework.common.filter.chain.FilterChain;
import cn.ly.service.base_framework.common.util.TimeThreadLocalUtil;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.InitializationException;
import com.timgroup.statsd.StatsDClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2018/10/23.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAspect {

    private static final Logger logger = LyLogger.getInstance(ServiceAspect.class);

    @Getter
    private FilterChain defaultFilterChain;

    @Setter
    private FilterChain filterChain;

    @Setter
    private FilterConfig filterConfig = new FilterConfig();

    @Autowired
    private StatsDClient statsDClient;

    @Around("target(cn.ly.service.base_framework.api.BaseFrameworkRestService) " +
            "&& execution(public * *(..)) " +
            "&& !@annotation(cn.ly.service.base_framework.common.annotation.IgnoreServiceAop)")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = LyJdk8DateUtil.getMilliSecondsTime();
        TimeThreadLocalUtil.set(startTime);
        StringBuilder reqArgsBuilder = buildArgs(joinPoint);

        FilterChain filterChain = null;
        try {
            filterChain = defaultFilterChain.cloneChain();
            Object retObj = filterChain.doFilter(joinPoint, filterChain);
            buildResultLog(retObj, reqArgsBuilder);
            return retObj;
        } catch (Exception e) {
            buildExceptionResultLog(e, reqArgsBuilder);
            throw e;
        } finally {
            if (Objects.nonNull(filterChain)) {
                filterChain.reset();
            }

            TimeThreadLocalUtil.remove();

            DBContext.clearDBKey();

            LyTraceLogUtil.clearTrace();

            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_REMOTE_IP);
            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_URI);
            LyMDCUtil.remove(LyMDCUtil.MDC_WEB_ELAPSED_TIME);
        }
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    private boolean isIgnoreLogMethod(ProceedingJoinPoint joinPoint) {
        String ignoreMethodName = filterConfig.getLog().getIgnoreMethodName();
        if (StringUtils.isNotBlank(ignoreMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    private StringBuilder buildArgs(ProceedingJoinPoint joinPoint) {
        StringBuilder sBuilder = new StringBuilder();
        if (isIgnoreLogMethod(joinPoint)) {
            return sBuilder;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        sBuilder.append("method => " + method.getName());
        sBuilder.append(", args => ");
        buildArgsLog(args, sBuilder);
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Optional.ofNullable(request).ifPresent(val -> {
            LyMDCUtil.put(LyMDCUtil.MDC_WEB_REMOTE_IP, LyNetworkUtil.getIpAddress(val));
            LyMDCUtil.put(LyMDCUtil.MDC_WEB_URI, val.getRequestURI());
        });

        return sBuilder;
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

    private void buildResultLog(Object retObj, StringBuilder sBuilder) {
        long elapsedMilliseconds = LyJdk8DateUtil.getMilliSecondsTime() - TimeThreadLocalUtil.get();
        if (retObj instanceof DataResult) {
            DataResult dataResult = (DataResult) retObj;
            dataResult.setElapsedMilliseconds(elapsedMilliseconds);
            sBuilder.append(" result => " + LyJsonUtil.toJson4Log(dataResult));
        } else if (retObj instanceof String) {
            sBuilder.append(" result => " + retObj);
        } else {
            sBuilder.append(" result => " + LyJsonUtil.toJson4Log(retObj));
        }
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_TIME, String.valueOf(elapsedMilliseconds));
        logger.info("请求响应日志: {}", sBuilder.toString());
    }

    private void buildExceptionResultLog(Exception e, StringBuilder sBuilder) {
        long elapsedMilliseconds = LyJdk8DateUtil.getMilliSecondsTime() - TimeThreadLocalUtil.get();
        sBuilder.append(" exception result => " + LyThrowableUtil.getStackTrace(e));
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_TIME, String.valueOf(elapsedMilliseconds));
        logger.error("请求响应日志: {}", sBuilder.toString());
    }

    @PostConstruct
    private void init() {
        if (Objects.isNull(statsDClient)) {
            throw new InitializationException("init Service Aspect exception[statsDClient is null]...");
        }
        defaultFilterChain = new FilterChain();
        boolean enabledDefaultFilter = filterConfig.isEnabledDefaultFilter();
        if (enabledDefaultFilter) {
            defaultFilterChain.addFilter(new FailFastFilter(filterConfig))
                    .addFilter(new TraceFilter())
                    .addFilter(new SignFilter(filterConfig))
                    .addFilter(new ParamValidateFilter())
                    .addFilter(new MetricsFilter(statsDClient));
        }
        if (Objects.nonNull(filterChain)) {
            defaultFilterChain.addFilter(filterChain.getFilters());
        }
        defaultFilterChain.sortFilters();
        LyMDCUtil.put(LyMDCUtil.MDC_WEB_ELAPSED_TIME, NumberUtils.INTEGER_ZERO.toString());
        logger.info("sort filter chain ===> {}", defaultFilterChain.printFilters());
    }
}
