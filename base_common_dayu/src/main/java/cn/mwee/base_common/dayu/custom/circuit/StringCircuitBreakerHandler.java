package cn.mwee.base_common.dayu.custom.circuit;

import cn.mwee.base_common.dayu.custom.breaker.StringCircuitBreaker;
import cn.mwee.base_common.dayu.custom.domain.CircuitBreakerDomain;
import cn.mwee.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import com.timgroup.statsd.StatsDClient;

import java.util.function.Supplier;

/**
 * Created by liaomengge on 2019/6/26.
 */
public class StringCircuitBreakerHandler extends CircuitBreakerHandler {

    public StringCircuitBreakerHandler(StatsDClient statsDClient, CircuitBreakerRedisHelper circuitBreakerRedisHelper) {
        super(statsDClient, circuitBreakerRedisHelper);
    }

    public String doHandle(String resource, Supplier<String> supplier) {
        return this.doHandle(CircuitBreakerDomain.builder().resource(resource).build(), supplier);
    }

    public String doHandle(CircuitBreakerDomain resource, Supplier<String> supplier) {
        return super.doHandle(resource, (StringCircuitBreaker) () -> supplier.get());
    }

    public String doHandle(String resource, StringCircuitBreaker circuitBreaker) {
        return this.doHandle(CircuitBreakerDomain.builder().resource(resource).build(), circuitBreaker);
    }

    public String doHandle(CircuitBreakerDomain resource, StringCircuitBreaker circuitBreaker) {
        return super.doHandle(resource, circuitBreaker);
    }
}
