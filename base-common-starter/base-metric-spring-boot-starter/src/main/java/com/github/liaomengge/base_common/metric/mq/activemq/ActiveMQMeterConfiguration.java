package com.github.liaomengge.base_common.metric.mq.activemq;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/29.
 */
@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, PooledConnectionFactory.class})
@ConditionalOnProperty(prefix = "base.metric.mq.activemq", name = "enabled", matchIfMissing = true)
public class ActiveMQMeterConfiguration {

    private final PooledConnectionFactory pooledConnectionFactory;

    public ActiveMQMeterConfiguration(ObjectProvider<PooledConnectionFactory> objectProvider) {
        this.pooledConnectionFactory = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public ActiveMQMeterBinder activeMQMeterBinder() {
        return new ActiveMQMeterBinder(pooledConnectionFactory);
    }
}
