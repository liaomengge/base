package com.github.liaomengge.service.base_framework.common.util;

import com.github.liaomengge.service.base_framework.common.annotation.IgnoreServiceApiLog;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2020/10/15.
 */
@UtilityClass
public class ServiceApiLogUtil {

    public Method getMethod(MethodInvocation invocation) {
        return invocation.getMethod();
    }

    public String getClassName(MethodInvocation invocation) {
        return getMethod(invocation).getDeclaringClass().getSimpleName();
    }

    public String getMethodName(MethodInvocation invocation) {
        return getMethod(invocation).getName();
    }

    public boolean isIgnoreAopLogHeader(MethodInvocation invocation) {
        Method method = getMethod(invocation);
        IgnoreServiceApiLog ignoreServiceApiLog =
                AnnotatedElementUtils.findMergedAnnotation(method, IgnoreServiceApiLog.class);
        return Optional.ofNullable(ignoreServiceApiLog).map(IgnoreServiceApiLog::ignoreHeader).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogHeader(MethodInvocation invocation, FilterConfig filterConfig) {
        String ignoreHeaderMethodName = filterConfig.getLog().getIgnoreHeaderMethodName();
        if (StringUtils.equalsIgnoreCase(ignoreHeaderMethodName, "*")) {
            return true;
        }
        if (StringUtils.isNotBlank(ignoreHeaderMethodName)) {
            String methodName = getMethodName(invocation);
            Iterable<String> iterable = SPLITTER.split(ignoreHeaderMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreAopLogRequest(MethodInvocation invocation) {
        Method method = getMethod(invocation);
        IgnoreServiceApiLog ignoreServiceApiLog =
                AnnotatedElementUtils.findMergedAnnotation(method, IgnoreServiceApiLog.class);
        return Optional.ofNullable(ignoreServiceApiLog).map(IgnoreServiceApiLog::ignoreArgs).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogRequest(MethodInvocation invocation, FilterConfig filterConfig) {
        String ignoreArgsMethodName = filterConfig.getLog().getIgnoreRequestMethodName();
        if (StringUtils.isNotBlank(ignoreArgsMethodName)) {
            String methodName = getMethodName(invocation);
            Iterable<String> iterable = SPLITTER.split(ignoreArgsMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreAopLogResponse(MethodInvocation invocation) {
        Method method = getMethod(invocation);
        IgnoreServiceApiLog ignoreServiceApiLog =
                AnnotatedElementUtils.findMergedAnnotation(method, IgnoreServiceApiLog.class);
        return Optional.ofNullable(ignoreServiceApiLog).map(IgnoreServiceApiLog::ignoreResult).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogResponse(MethodInvocation invocation, FilterConfig filterConfig) {
        String ignoreResultMethodName = filterConfig.getLog().getIgnoreResponseMethodName();
        if (StringUtils.isNotBlank(ignoreResultMethodName)) {
            String methodName = getMethodName(invocation);
            Iterable<String> iterable = SPLITTER.split(ignoreResultMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }
}
