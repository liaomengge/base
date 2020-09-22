package cn.ly.base_common.metric.http.okhttp3;

import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/17.
 */
@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, OkHttpClient.class})
@ConditionalOnProperty(prefix = "ly.metric.http.okhttp3", name = "enabled", matchIfMissing = true)
public class Okhttp3MetricsConfiguration {

    private final OkHttpClient okHttpClient;

    public Okhttp3MetricsConfiguration(ObjectProvider<OkHttpClient> provider) {
        this.okHttpClient = provider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public Okhttp3MetricsBinder okhttp3MetricsBinder() {
        return new Okhttp3MetricsBinder(okHttpClient);
    }
}
