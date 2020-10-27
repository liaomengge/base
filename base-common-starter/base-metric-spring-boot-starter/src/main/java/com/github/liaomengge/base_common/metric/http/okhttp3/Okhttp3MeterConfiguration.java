package com.github.liaomengge.base_common.metric.http.okhttp3;

import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.ConnectionPool;
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
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, OkHttpClient.class})
@ConditionalOnProperty(prefix = "base.metric.http.okhttp3", name = "enabled", matchIfMissing = true)
public class Okhttp3MeterConfiguration {

    private final ConnectionPool connectionPool;

    public Okhttp3MeterConfiguration(ObjectProvider<ConnectionPool> provider) {
        this.connectionPool = provider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public Okhttp3MeterBinder okhttp3MeterBinder() {
        return new Okhttp3MeterBinder(connectionPool);
    }
}
