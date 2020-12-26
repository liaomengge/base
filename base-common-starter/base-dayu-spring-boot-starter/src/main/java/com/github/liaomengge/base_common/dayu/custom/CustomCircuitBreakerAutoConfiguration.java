package com.github.liaomengge.base_common.dayu.custom;

import com.github.liaomengge.base_common.dayu.custom.CustomCircuitBreakerProperties.RuleProperties;
import com.github.liaomengge.base_common.dayu.custom.aspect.CircuitBreakerResourceAspect;
import com.github.liaomengge.base_common.dayu.custom.circuit.CircuitBreakerHandler;
import com.github.liaomengge.base_common.dayu.custom.config.CircuitBreakerConfig;
import com.github.liaomengge.base_common.dayu.custom.consts.CustomCircuitBreakerConst;
import com.github.liaomengge.base_common.dayu.custom.helper.CircuitBreakerRedisHelper;
import com.github.liaomengge.base_common.helper.redis.IRedisHelper;
import com.github.liaomengge.base_common.redis.RedisAutoConfiguration;
import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/6/26.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = CustomCircuitBreakerConst.CUSTOM_CIRCUIT_BREAKER_PREFIX, name = "enabled")
@EnableConfigurationProperties(CustomCircuitBreakerProperties.class)
public class CustomCircuitBreakerAutoConfiguration {

    private final CustomCircuitBreakerProperties customCircuitBreakerProperties;

    @RefreshScope
    @Bean
    @ConditionalOnBean(IRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerRedisHelper.class)
    public CircuitBreakerRedisHelper circuitBreakerRedisHelper(IRedisHelper iRedisHelper) {
        RuleProperties ruleProperties = customCircuitBreakerProperties.getRule();
        CircuitBreakerConfig circuitBreakerConfig = new CircuitBreakerConfig();
        circuitBreakerConfig.setFailureIntervalSeconds(LyNumberUtil.getIntValue(ruleProperties.getFailureIntervalSeconds()));
        circuitBreakerConfig.setFailureThreshold(LyNumberUtil.getIntValue(ruleProperties.getFailureThreshold()));
        circuitBreakerConfig.setResetMilliSeconds(LyNumberUtil.getIntValue(ruleProperties.getResetMilliSeconds()));
        return new CircuitBreakerRedisHelper(iRedisHelper, circuitBreakerConfig);
    }

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(CircuitBreakerRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerHandler.class)
    public CircuitBreakerHandler circuitHandler(MeterRegistry meterRegistry,
                                                CircuitBreakerRedisHelper circuitBreakerRedisHelper) {
        return new CircuitBreakerHandler(meterRegistry, circuitBreakerRedisHelper);
    }

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnBean(CircuitBreakerRedisHelper.class)
    @ConditionalOnMissingBean(CircuitBreakerResourceAspect.class)
    public CircuitBreakerResourceAspect circuitBreakerResourceAspect(MeterRegistry meterRegistry,
                                                                     CircuitBreakerRedisHelper circuitBreakerRedisHelper) {
        return new CircuitBreakerResourceAspect(meterRegistry, circuitBreakerRedisHelper);
    }
}
