package cn.ly.base_common.dayu.custom.aspect;

import cn.ly.base_common.dayu.custom.annotation.CircuitBreakerResource;
import cn.ly.base_common.dayu.custom.consts.CircuitBreakerConst;
import cn.ly.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import cn.ly.base_common.support.exception.CircuitBreakerException;
import cn.ly.base_common.support.meter._MeterRegistrys;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by liaomengge on 2019/10/30.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@AllArgsConstructor
public class CircuitBreakerResourceAspect extends AbstractAspectSupport {

    private MeterRegistry meterRegistry;
    private CircuitBreakerRedisHelper circuitBreakerRedisHelper;

    @Pointcut("@annotation(cn.ly.base_common.dayu.custom.annotation.CircuitBreakerResource)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (Objects.isNull(method)) {
            return joinPoint.proceed();
        }
        CircuitBreakerResource circuitBreakerResource = method.getAnnotation(CircuitBreakerResource.class);
        if (Objects.isNull(circuitBreakerResource)) {
            return joinPoint.proceed();
        }
        String resource = circuitBreakerResource.value();
        if (StringUtils.isBlank(resource)) {
            return joinPoint.proceed();
        }
        int failureCount = 0;
        try {
            failureCount = circuitBreakerRedisHelper.getFailureCount(resource);
            long latestFailureTime = circuitBreakerRedisHelper.getLatestFailureTime(resource);
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                if ((LyJdk8DateUtil.getMilliSecondsTime() - latestFailureTime) <= circuitBreakerRedisHelper.getCircuitBreakerConfig().getResetMilliSeconds()) {
                    //open status
                    log.warn("Resource[{}], Custom Circuit Open...", resource);
                    _MeterRegistrys.counter(meterRegistry,
                            CircuitBreakerConst.Metric.CIRCUIT_BREAKER_PREFIX + resource).ifPresent(Counter::increment);
                    return super.handleFallback(joinPoint, circuitBreakerResource);
                }
                //half open status
            }
            //close status
            return joinPoint.proceed();
        } catch (CircuitBreakerException e) {
            log.warn("Resource[{}], request fallback handle ==> {}", resource, LyThrowableUtil.getStackTrace(e));
            throw e;
        } catch (Throwable t) {
            log.warn("Resource[{}], request custom circuit handle failed ==> {}", resource,
                    LyThrowableUtil.getStackTrace(t));
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                circuitBreakerRedisHelper.getIRedisHelper().set(circuitBreakerRedisHelper.getLatestFailureTimeStr(resource),
                        String.valueOf(LyJdk8DateUtil.getMilliSecondsTime()));
                throw t;
            }

            circuitBreakerRedisHelper.incrFailureCount(resource);
            throw t;
        }
    }
}
