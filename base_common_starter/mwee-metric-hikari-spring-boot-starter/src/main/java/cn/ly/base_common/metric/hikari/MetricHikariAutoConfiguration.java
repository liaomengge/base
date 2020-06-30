package cn.ly.base_common.metric.hikari;

import cn.ly.base_common.metric.hikari.task.MetricHikariScheduledTask;
import com.timgroup.statsd.StatsDClient;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by liaomengge on 2019/7/24.
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnProperty(prefix = "mwee.metric-hikari", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricHikariProperties.class)
public class MetricHikariAutoConfiguration {

    private final List<HikariDataSource> hikariDataSources;

    public MetricHikariAutoConfiguration(ObjectProvider<List<HikariDataSource>> objectProvider) {
        this.hikariDataSources = objectProvider.getIfAvailable();
    }

    @Autowired
    private MetricHikariProperties metricHikariProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricHikariScheduledTask metricHikariScheduledTask(StatsDClient statsDClient) {
        return new MetricHikariScheduledTask(statsDClient, metricHikariProperties, hikariDataSources);
    }
}
