package com.github.liaomengge.base_common.metric.threadpool;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/18.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(MeterRegistry.class)
@ConditionalOnProperty(prefix = "base.metric.threadpool", name = "enabled", matchIfMissing = true)
public class ThreadPoolMetricsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolMetricsBinder threadPoolMetricsBinder() {
        return new ThreadPoolMetricsBinder();
    }
}
