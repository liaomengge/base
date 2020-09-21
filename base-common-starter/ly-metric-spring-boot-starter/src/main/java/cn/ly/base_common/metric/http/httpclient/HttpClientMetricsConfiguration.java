package cn.ly.base_common.metric.http.httpclient;

import cn.ly.base_common.metric.MetricProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/17.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnBean(MeterRegistry.class)
public class HttpClientMetricsConfiguration {

    private final MetricProperties metricProperties;
    private final PoolingHttpClientConnectionManager poolConnManager;

    public HttpClientMetricsConfiguration(MetricProperties metricProperties,
                                          ObjectProvider<PoolingHttpClientConnectionManager> provider) {
        this.metricProperties = metricProperties;
        this.poolConnManager = provider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(PoolingHttpClientConnectionManager.class)
    public HttpClientMetricsBinder httpClientMetricsBinder() {
        return new HttpClientMetricsBinder(metricProperties, poolConnManager);
    }
}
