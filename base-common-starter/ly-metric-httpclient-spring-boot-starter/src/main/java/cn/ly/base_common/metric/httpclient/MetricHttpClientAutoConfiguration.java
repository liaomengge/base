package cn.ly.base_common.metric.httpclient;

import cn.ly.base_common.metric.httpclient.task.MetricHttpClientScheduledTask;

import com.timgroup.statsd.StatsDClient;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Configuration
@ConditionalOnClass(PoolingHttpClientConnectionManager.class)
@ConditionalOnProperty(prefix = "ly.metric-http-client", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricHttpClientProperties.class)
public class MetricHttpClientAutoConfiguration {

    private final PoolingHttpClientConnectionManager poolConnManager;

    public MetricHttpClientAutoConfiguration(ObjectProvider<PoolingHttpClientConnectionManager> objectProvider) {
        this.poolConnManager = objectProvider.getIfAvailable();
    }

    @Autowired
    private MetricHttpClientProperties metricHttpClientProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricHttpClientScheduledTask metricHttpClientScheduledTask(StatsDClient statsDClient) {
        return new MetricHttpClientScheduledTask(statsDClient, metricHttpClientProperties, poolConnManager);
    }
}
