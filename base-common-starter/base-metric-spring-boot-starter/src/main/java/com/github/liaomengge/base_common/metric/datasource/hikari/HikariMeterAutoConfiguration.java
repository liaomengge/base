package com.github.liaomengge.base_common.metric.datasource.hikari;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by liaomengge on 2020/9/17.
 * hikari本身也提供了setMetricRegistry,setMetricsTrackerFactory方法，作为监控的扩展点，比如：PrometheusHistogramMetricsTrackerFactory
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, HikariDataSource.class})
@ConditionalOnProperty(prefix = "base.metric.datasource.hikari", name = "enabled", matchIfMissing = true)
public class HikariMeterAutoConfiguration {

    private final List<DataSource> dataSources;

    public HikariMeterAutoConfiguration(ObjectProvider<List<DataSource>> objectProvider) {
        this.dataSources = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public HikariMeterBinder hikariMeterBinder() {
        return new HikariMeterBinder(dataSources);
    }
}
