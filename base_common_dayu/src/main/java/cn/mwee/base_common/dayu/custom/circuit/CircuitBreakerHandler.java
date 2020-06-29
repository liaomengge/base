package cn.mwee.base_common.dayu.custom.circuit;

import cn.mwee.base_common.dayu.custom.breaker.CircuitBreaker;
import cn.mwee.base_common.dayu.custom.consts.CircuitBreakerConst.Metric;
import cn.mwee.base_common.dayu.custom.domain.CircuitBreakerDomain;
import cn.mwee.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.error.MwThrowableUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Created by liaomengge on 2019/6/26.
 */
@AllArgsConstructor
public class CircuitBreakerHandler {

    private static final Logger logger = MwLogger.getInstance(CircuitBreakerHandler.class);

    private StatsDClient statsDClient;
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
                if ((MwJdk8DateUtil.getMilliSecondsTime() - latestFailureTime) <= circuitBreakerRedisHelper.getCircuitBreakerConfig().getResetMilliSeconds()) {
                    //open status
                    logger.warn("Resource[{}], Custom Circuit Open...", resource);
                    Optional.ofNullable(statsDClient).ifPresent(val -> statsDClient.increment(Metric.CIRCUIT_BREAKER_PREFIX + resource));
                    return circuitBreaker.fallback();
                }
                //half open status
            }
            //close status
            result = circuitBreaker.execute();
        } catch (Throwable t) {
            logger.warn("Resource[{}], request custom circuit handle failed ==> {}", resource,
                    MwThrowableUtil.getStackTrace(t));
            if (failureCount >= circuitBreakerRedisHelper.getCircuitBreakerConfig().getFailureThreshold()) {
                circuitBreakerRedisHelper.getIRedisHelper().set(circuitBreakerRedisHelper.getLatestFailureTimeStr(resource),
                        String.valueOf(MwJdk8DateUtil.getMilliSecondsTime()));
                throw t;
            }
            //todo 是否可以交换下位置
            circuitBreakerRedisHelper.incrFailureCount(resource);
            throw t;
        }
        return result;
    }
}
