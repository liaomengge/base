package cn.ly.base_common.metric.datasource.hikari;

import com.zaxxer.hikari.HikariDataSource;
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
 * hikari本身也提供了setMetricRegistry,setMetricsTrackerFactory方法，作为监控的扩展点，比如：PrometheusHistogramMetricsTrackerFactory
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(MeterRegistry.class)
public class HikariMetricsConfiguration {

    private final List<DataSource> dataSources;

    public HikariMetricsConfiguration(ObjectProvider<List<DataSource>> objectProvider) {
        this.dataSources = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(HikariDataSource.class)
    public HikariMetricsBinder hikariMetricsBinder() {
        return new HikariMetricsBinder(dataSources);
    }
}
