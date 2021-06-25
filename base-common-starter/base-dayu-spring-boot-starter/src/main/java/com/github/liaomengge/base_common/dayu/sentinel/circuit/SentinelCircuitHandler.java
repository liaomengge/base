package com.github.liaomengge.base_common.dayu.sentinel.circuit;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.github.liaomengge.base_common.dayu.consts.DayuConst;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Created by liaomengge on 2019/10/30.
 */
@Slf4j
@AllArgsConstructor
public class SentinelCircuitHandler {

    private MeterRegistry meterRegistry;

    public <R> R doHandle(String resource, SentinelCircuitBreaker<R> circuitBreaker) {
        if (StringUtils.isBlank(resource)) {
            return circuitBreaker.execute();
        }
        R result;
        Entry sentinelEntry = null;
        try {
            sentinelEntry = SphU.entry(resource, EntryType.OUT);
            result = circuitBreaker.execute();
        } catch (BlockException e) {
            result = handleBlockException(resource, circuitBreaker, e);
        } catch (Throwable t) {
            log.warn("Resource[{}], request sentinel circuit handle failed ==> {}", resource,
                    LyThrowableUtil.getStackTrace(t));
            Tracer.trace(t);
            throw t;
        } finally {
            Optional.ofNullable(sentinelEntry).ifPresent(val -> val.exit());
        }
        return result;
    }

    private <R> R handleBlockException(String resource, SentinelCircuitBreaker<R> circuitBreaker, BlockException e) {
        if (e instanceof DegradeException || LyExceptionUtil.unwrap(e) instanceof DegradeException) {
            _MeterRegistrys.counter(meterRegistry, DayuConst.METRIC_SENTINEL_FALLBACK_PREFIX + resource).ifPresent(Counter::increment);
            return circuitBreaker.fallback();
        }
        _MeterRegistrys.counter(meterRegistry, DayuConst.METRIC_SENTINEL_BLOCKED_PREFIX + resource).ifPresent(Counter::increment);
        return circuitBreaker.block();
    }
}
