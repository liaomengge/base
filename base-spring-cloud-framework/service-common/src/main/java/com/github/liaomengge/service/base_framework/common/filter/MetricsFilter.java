package com.github.liaomengge.service.base_framework.common.filter;

import com.github.liaomengge.service.base_framework.common.consts.MetricsConst;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.github.liaomengge.service.base_framework.common.util.TimeThreadLocalUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.annotation.Order;

import java.time.Duration;
import java.util.Optional;

/**
 * Created by liaomengge on 2018/11/22.
 */
@Order(3000)
@AllArgsConstructor
public class MetricsFilter extends AbstractFilter {

    private final MeterRegistry meterRegistry;

    @Override
    public Object doFilter(ProceedingJoinPoint joinPoint, FilterChain chain) throws Throwable {
        boolean isSuccess = true;
        try {
            return chain.doFilter(joinPoint, chain);
        } catch (Exception e) {
            isSuccess = false;
            throw e;
        } finally {
            String prefix = super.getMethodName(joinPoint);
            this.statRestExec(prefix, isSuccess);
        }
    }

    private void statRestExec(String prefix, boolean isSuccess) {
        Optional.ofNullable(meterRegistry).ifPresent(val -> {
            if (isSuccess) {
                val.counter(prefix + MetricsConst.REQ_EXE_SUC).increment();
                val.counter(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_SUC).increment();
            } else {
                val.counter(prefix + MetricsConst.REQ_EXE_FAIL).increment();
                val.counter(MetricsConst.REQ_ALL + MetricsConst.REQ_EXE_FAIL).increment();
            }
            long elapsedNanoTime = System.nanoTime() - TimeThreadLocalUtil.get();
            val.timer(prefix + MetricsConst.REQ_EXE_TIME).record(Duration.ofNanos(elapsedNanoTime));
        });
    }
}
