package cn.ly.base_common.metric.retrofit;

import cn.ly.base_common.metric.retrofit.task.MetricRetrofitScheduledTask;

import com.timgroup.statsd.StatsDClient;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Configuration
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(prefix = "ly.metric-retrofit", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricRetrofitProperties.class)
public class MetricRetrofitAutoConfiguration {

    private final OkHttpClient okHttpClient;
    private final MetricRetrofitProperties metricRetrofitProperties;

    public MetricRetrofitAutoConfiguration(ObjectProvider<OkHttpClient> objectProvider,
                                           MetricRetrofitProperties metricRetrofitProperties) {
        this.okHttpClient = objectProvider.getIfAvailable();
        this.metricRetrofitProperties = metricRetrofitProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public MetricRetrofitScheduledTask metricRetrofitScheduledTask(StatsDClient statsDClient) {
        return new MetricRetrofitScheduledTask(statsDClient, metricRetrofitProperties, okHttpClient);
    }
}
