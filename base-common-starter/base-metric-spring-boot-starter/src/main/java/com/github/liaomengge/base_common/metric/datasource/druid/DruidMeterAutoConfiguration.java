package com.github.liaomengge.base_common.metric.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
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
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, DruidDataSource.class})
@ConditionalOnProperty(prefix = "base.metric.datasource.druid", name = "enabled", matchIfMissing = true)
public class DruidMeterAutoConfiguration {

    private final List<DataSource> dataSources;

    public DruidMeterAutoConfiguration(ObjectProvider<List<DataSource>> objectProvider) {
        this.dataSources = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public DruidMeterBinder druidMeterBinder() {
        return new DruidMeterBinder(dataSources);
    }
}
