package cn.ly.base_common.metric.retrofit;

import cn.ly.base_common.metric.retrofit.task.MetricRetrofitScheduledTask;
import com.timgroup.statsd.StatsDClient;
import okhttp3.OkHttpClient;
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
@ConditionalOnClass(OkHttpClient.class)
@ConditionalOnProperty(prefix = "ly.metric-retrofit", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricRetrofitProperties.class)
public class MetricRetrofitAutoConfiguration {

    private final OkHttpClient okHttpClient;

    public MetricRetrofitAutoConfiguration(ObjectProvider<OkHttpClient> objectProvider) {
        this.okHttpClient = objectProvider.getIfAvailable();
    }

    @Autowired
    private MetricRetrofitProperties metricRetrofitProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricRetrofitScheduledTask metricRetrofitScheduledTask(StatsDClient statsDClient) {
        return new MetricRetrofitScheduledTask(statsDClient, metricRetrofitProperties, okHttpClient);
    }
}
