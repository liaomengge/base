package com.github.liaomengge.base_common.dayu.custom.aspect;

import com.github.liaomengge.base_common.dayu.custom.annotation.CircuitBreakerResource;
import com.github.liaomengge.base_common.support.exception.CircuitBreakerException;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by liaomengge on 2019/10/30.
 */
public abstract class AbstractAspectSupport {

    protected static final Logger log = LyLogger.getInstance(AbstractAspectSupport.class);

    protected Object handleFallback(ProceedingJoinPoint pjp, CircuitBreakerResource circuitBreakerResource) throws Throwable {
        String fallback = circuitBreakerResource.fallback();
        Class<?> fallbackClass = circuitBreakerResource.fallbackClass();
        if (StringUtils.isNotBlank(fallback) && Objects.nonNull(fallbackClass)) {
            Method method = ReflectionUtils.findMethod(circuitBreakerResource.fallbackClass(),
                    circuitBreakerResource.fallback());
            if (Objects.nonNull(method)) {
                return ReflectionUtils.invokeMethod(method, pjp.getTarget());
            }
        }
        log.warn("fallback method not found!!!");
        throw new CircuitBreakerException("000700", "熔断异常");
    }
}
