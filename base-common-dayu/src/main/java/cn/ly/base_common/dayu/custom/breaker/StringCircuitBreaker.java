package cn.ly.base_common.dayu.custom.breaker;

/**
 * Created by liaomengge on 2019/6/26.
 */
public interface StringCircuitBreaker extends CircuitBreaker<String> {

    @Override
    default String fallback() {
        return "MW_DAYU_CUSTOM_FALLBACK";
    }
}
