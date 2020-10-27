package com.github.liaomengge.base_common.dayu;

import com.github.liaomengge.base_common.dayu.custom.CustomCircuitBreakerConfiguration;
import com.github.liaomengge.base_common.dayu.guava.GuavaRateLimitConfiguration;
import com.github.liaomengge.base_common.dayu.sentinel.SentinelConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/8/9.
 */
@Configuration(proxyBeanMethods = false)
@Import({SentinelConfiguration.class, GuavaRateLimitConfiguration.class, CustomCircuitBreakerConfiguration.class})
public class DaYuAutoConfiguration {
}
