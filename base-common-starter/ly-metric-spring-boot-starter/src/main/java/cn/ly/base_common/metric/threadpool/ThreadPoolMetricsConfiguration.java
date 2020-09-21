package cn.ly.base_common.metric.threadpool;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/18.
 */
@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(MeterRegistry.class)
public class ThreadPoolMetricsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolMetricsBinder executorServiceMetricsBinder() {
        return new ThreadPoolMetricsBinder();
    }
}
