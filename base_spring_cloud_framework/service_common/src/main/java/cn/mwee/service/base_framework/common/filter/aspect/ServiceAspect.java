package cn.mwee.service.base_framework.common.filter.aspect;

import cn.mwee.base_common.support.datasource.DBContext;
import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.error.MwThrowableUtil;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import cn.mwee.base_common.utils.log.MwMDCUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.net.MwNetworkUtil;
import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import cn.mwee.base_common.utils.web.MwWebUtil;
import cn.mwee.service.base_framework.base.DataResult;
import cn.mwee.service.base_framework.common.config.FilterConfig;
import cn.mwee.service.base_framework.common.filter.*;
import cn.mwee.service.base_framework.common.filter.chain.FilterChain;
import cn.mwee.service.base_framework.common.util.TimeThreadLocalUtil;
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

import static cn.mwee.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2018/10/23.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServiceAspect {

    private static final Logger logger = MwLogger.getInstance(ServiceAspect.class);

    @Getter
    private FilterChain defaultFilterChain;

    @Setter
    private FilterChain filterChain;

    @Setter
    private FilterConfig filterConfig = new FilterConfig();

    @Autowired
    private StatsDClient statsDClient;

    @Around("target(cn.mwee.service.base_framework.api.BaseFrameworkRestService) " +
            "&& execution(public * *(..)) " +
            "&& !@annotation(cn.mwee.service.base_framework.common.annotation.IgnoreServiceAop)")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = MwJdk8DateUtil.getMilliSecondsTime();
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

            MwTraceLogUtil.clearTrace();

            MwMDCUtil.remove(MwMDCUtil.MDC_WEB_REMOTE_IP);
            MwMDCUtil.remove(MwMDCUtil.MDC_WEB_URI);
            MwMDCUtil.remove(MwMDCUtil.MDC_WEB_ELAPSED_TIME);
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
            MwMDCUtil.put(MwMDCUtil.MDC_WEB_REMOTE_IP, MwNetworkUtil.getIpAddress(val));
            MwMDCUtil.put(MwMDCUtil.MDC_WEB_URI, val.getRequestURI());
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
                        sBuilder.append(MwJsonUtil.toJson4Log(MwWebUtil.getRequestParams((HttpServletRequest) val))).append(',');
                    } else if (val instanceof WebRequest) {
                        sBuilder.append(MwJsonUtil.toJson4Log(((WebRequest) val).getParameterMap())).append(',');
                    } else {
                        sBuilder.append(MwJsonUtil.toJson4Log(val)).append(',');
                    }
                });
    }

    private void buildResultLog(Object retObj, StringBuilder sBuilder) {
        long elapsedMilliseconds = MwJdk8DateUtil.getMilliSecondsTime() - TimeThreadLocalUtil.get();
        if (retObj instanceof DataResult) {
            DataResult dataResult = (DataResult) retObj;
            dataResult.setElapsedMilliseconds(elapsedMilliseconds);
            sBuilder.append(" result => " + MwJsonUtil.toJson4Log(dataResult));
        } else if (retObj instanceof String) {
            sBuilder.append(" result => " + retObj);
        } else {
            sBuilder.append(" result => " + MwJsonUtil.toJson4Log(retObj));
        }
        MwMDCUtil.put(MwMDCUtil.MDC_WEB_ELAPSED_TIME, String.valueOf(elapsedMilliseconds));
        logger.info("请求响应日志: {}", sBuilder.toString());
    }

    private void buildExceptionResultLog(Exception e, StringBuilder sBuilder) {
        long elapsedMilliseconds = MwJdk8DateUtil.getMilliSecondsTime() - TimeThreadLocalUtil.get();
        sBuilder.append(" exception result => " + MwThrowableUtil.getStackTrace(e));
        MwMDCUtil.put(MwMDCUtil.MDC_WEB_ELAPSED_TIME, String.valueOf(elapsedMilliseconds));
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
        MwMDCUtil.put(MwMDCUtil.MDC_WEB_ELAPSED_TIME, NumberUtils.INTEGER_ZERO.toString());
        logger.info("sort filter chain ===> {}", defaultFilterChain.printFilters());
    }
}
