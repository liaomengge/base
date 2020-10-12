package cn.ly.base_common.dayu.sentinel.circuit;

import cn.ly.base_common.dayu.consts.DayuConst;
import cn.ly.base_common.utils.error.LyExceptionUtil;
import cn.ly.base_common.utils.error.LyThrowableUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Created by liaomengge on 2019/10/30.
 */
@AllArgsConstructor
public class SentinelCircuitHandler {

    private static final Logger log = LyLogger.getInstance(SentinelCircuitHandler.class);

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
            Optional.ofNullable(meterRegistry).ifPresent(val -> val.counter(DayuConst.METRIC_SENTINEL_FALLBACK_PREFIX + resource).increment());
            return circuitBreaker.fallback();
        }
        Optional.ofNullable(meterRegistry).ifPresent(val -> val.counter(DayuConst.METRIC_SENTINEL_BLOCKED_PREFIX + resource).increment());
        return circuitBreaker.block();
    }
}
