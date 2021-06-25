package com.github.liaomengge.base_common.dayu.custom.circuit;

import com.github.liaomengge.base_common.dayu.custom.breaker.CircuitBreaker;
import com.github.liaomengge.base_common.dayu.custom.consts.CircuitBreakerConst;
import com.github.liaomengge.base_common.dayu.custom.domain.CircuitBreakerDomain;
import com.github.liaomengge.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liaomengge on 2019/6/26.
 */
@Slf4j
@AllArgsConstructor
public class CircuitBreakerHandler {

    private MeterRegistry meterRegistry;
    private CircuitBreakerRedisHelper circuitBreakerRedisHelper;

    public <R> R doHandle(String resource, CircuitBreaker<R> circuitBreaker) {
        return this.doHandle(CircuitBreakerDomain.builder().resource(resource).build(), circuitBreaker);
    }

    public <R> R doHandle(CircuitBreakerDomain circuitBreakerDomain, CircuitBreaker<R> circuitBreaker) {
        R result;
        int failureCount = 0;
        String resource = circuitBreakerDomain.getResource();
        try {
            if (StringUtils.isBlank(resource)) {
                return circuitBreaker.execute();
            }
            failureCount = circuitBreakerRedisHelper.getFailureCount(resource);
            long latestFailureTime = circuitBreakerRedisHelper.getLatestFailureTime(resource);
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                if ((LyJdk8DateUtil.getMilliSecondsTime() - latestFailureTime) <= circuitBreakerRedisHelper.getCircuitBreakerConfig().getResetMilliSeconds()) {
                    //open status
                    log.warn("Resource[{}], Custom Circuit Open...", resource);
                    _MeterRegistrys.counter(meterRegistry,
                            CircuitBreakerConst.MetricConst.CIRCUIT_BREAKER_PREFIX + resource).ifPresent(Counter::increment);
                    return circuitBreaker.fallback();
                }
                //half open status
            }
            //close status
            result = circuitBreaker.execute();
        } catch (Throwable t) {
            log.warn("Resource[{}], request custom circuit handle failed ==> {}", resource,
                    LyThrowableUtil.getStackTrace(t));
            circuitBreakerRedisHelper.incrFailureCount(resource);
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                circuitBreakerRedisHelper.getIRedisHelper().set(circuitBreakerRedisHelper.getLatestFailureTimeKey(resource),
                        String.valueOf(LyJdk8DateUtil.getMilliSecondsTime()));
                throw t;
            }
            throw t;
        }
        return result;
    }
}
