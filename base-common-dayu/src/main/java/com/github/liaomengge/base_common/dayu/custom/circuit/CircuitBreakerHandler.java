package com.github.liaomengge.base_common.dayu.custom.circuit;

import com.github.liaomengge.base_common.dayu.custom.breaker.CircuitBreaker;
import com.github.liaomengge.base_common.dayu.custom.consts.CircuitBreakerConst;
import com.github.liaomengge.base_common.dayu.custom.domain.CircuitBreakerDomain;
import com.github.liaomengge.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 2019/6/26.
 */
@AllArgsConstructor
public class CircuitBreakerHandler {

    private static final Logger log = LyLogger.getInstance(CircuitBreakerHandler.class);

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
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                circuitBreakerRedisHelper.getIRedisHelper().set(circuitBreakerRedisHelper.getLatestFailureTimeStr(resource),
                        String.valueOf(LyJdk8DateUtil.getMilliSecondsTime()));
                throw t;
            }
            //todo 是否可以交换下位置
            circuitBreakerRedisHelper.incrFailureCount(resource);
            throw t;
        }
        return result;
    }
}
