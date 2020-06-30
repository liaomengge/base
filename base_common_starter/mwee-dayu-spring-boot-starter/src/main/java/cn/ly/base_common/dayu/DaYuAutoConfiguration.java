package cn.ly.base_common.dayu;

import cn.ly.base_common.dayu.custom.CustomCircuitBreakerConfiguration;
import cn.ly.base_common.dayu.guava.GuavaRateLimitConfiguration;
import cn.ly.base_common.dayu.sentinel.SentinelConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/8/9.
 */
@Configuration
@Import({SentinelConfiguration.class, GuavaRateLimitConfiguration.class, CustomCircuitBreakerConfiguration.class})
public class DaYuAutoConfiguration {
}
