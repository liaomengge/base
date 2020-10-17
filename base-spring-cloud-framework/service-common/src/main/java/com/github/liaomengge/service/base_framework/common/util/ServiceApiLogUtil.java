package com.github.liaomengge.service.base_framework.common.util;

import com.github.liaomengge.service.base_framework.common.annotation.IgnoreServiceApiAop;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Optional;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2020/10/15.
 */
@UtilityClass
public class ServiceApiLogUtil {

    public Method getMethod(ProceedingJoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    public String getMethodName(ProceedingJoinPoint joinPoint) {
        return getMethod(joinPoint).getName();
    }

    public boolean isIgnoreAopLogHeaderMethod(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        IgnoreServiceApiAop ignoreServiceApiAop = method.getAnnotation(IgnoreServiceApiAop.class);
        return Optional.ofNullable(ignoreServiceApiAop).map(IgnoreServiceApiAop::ignoreHeader).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogHeaderMethod(ProceedingJoinPoint joinPoint, FilterConfig filterConfig) {
        String ignoreHeaderMethodName = filterConfig.getLog().getIgnoreHeaderMethodName();
        if (StringUtils.equalsIgnoreCase(ignoreHeaderMethodName, "*")) {
            return true;
        }
        if (StringUtils.isNotBlank(ignoreHeaderMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreHeaderMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreAopLogArgsMethod(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        IgnoreServiceApiAop ignoreServiceApiAop = method.getAnnotation(IgnoreServiceApiAop.class);
        return Optional.ofNullable(ignoreServiceApiAop).map(IgnoreServiceApiAop::ignoreArgs).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogArgsMethod(ProceedingJoinPoint joinPoint, FilterConfig filterConfig) {
        String ignoreArgsMethodName = filterConfig.getLog().getIgnoreArgsMethodName();
        if (StringUtils.isNotBlank(ignoreArgsMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreArgsMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }

    public boolean isIgnoreAopLogResultMethod(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        IgnoreServiceApiAop ignoreServiceApiAop = method.getAnnotation(IgnoreServiceApiAop.class);
        return Optional.ofNullable(ignoreServiceApiAop).map(IgnoreServiceApiAop::ignoreResult).orElse(Boolean.FALSE);
    }

    public boolean isIgnoreLogResultMethod(ProceedingJoinPoint joinPoint, FilterConfig filterConfig) {
        String ignoreResultMethodName = filterConfig.getLog().getIgnoreResultMethodName();
        if (StringUtils.isNotBlank(ignoreResultMethodName)) {
            String methodName = getMethodName(joinPoint);
            Iterable<String> iterable = SPLITTER.split(ignoreResultMethodName);
            return Iterables.contains(iterable, methodName);
        }
        return false;
    }
}
