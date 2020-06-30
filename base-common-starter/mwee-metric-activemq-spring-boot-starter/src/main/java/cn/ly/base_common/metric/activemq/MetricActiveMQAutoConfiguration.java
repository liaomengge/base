package cn.ly.base_common.metric.activemq;

import cn.ly.base_common.metric.activemq.task.MetricActiveMQScheduledTask;
import com.timgroup.statsd.StatsDClient;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Configuration
@ConditionalOnClass(PooledConnectionFactory.class)
@ConditionalOnProperty(prefix = "mwee.metric-activemq", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricActiveMQProperties.class)
public class MetricActiveMQAutoConfiguration {

    private final PooledConnectionFactory pooledConnectionFactory;

    public MetricActiveMQAutoConfiguration(ObjectProvider<PooledConnectionFactory> objectProvider) {
        this.pooledConnectionFactory = objectProvider.getIfAvailable();
    }

    @Autowired
    private MetricActiveMQProperties metricActiveMQProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricActiveMQScheduledTask metricActiveMQScheduledTask(StatsDClient statsDClient) {
        return new MetricActiveMQScheduledTask(statsDClient, pooledConnectionFactory, metricActiveMQProperties);
    }
}
