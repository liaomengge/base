package cn.ly.base_common.metric.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by liaomengge on 2020/9/17.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(MeterRegistry.class)
public class DruidMetricsConfiguration {

    private final List<DataSource> dataSources;

    public DruidMetricsConfiguration(ObjectProvider<List<DataSource>> objectProvider) {
        this.dataSources = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DruidDataSource.class)
    public DruidMetricsBinder druidMetricsBinder() {
        return new DruidMetricsBinder(dataSources);
    }
}