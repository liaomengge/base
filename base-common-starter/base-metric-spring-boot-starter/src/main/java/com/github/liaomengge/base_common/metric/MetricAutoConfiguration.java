package com.github.liaomengge.base_common.metric;

import com.github.liaomengge.base_common.metric.cache.local.LocalCacheMeterConfiguration;
import com.github.liaomengge.base_common.metric.cache.redis.RedisCacheMeterConfiguration;
import com.github.liaomengge.base_common.metric.datasource.druid.DruidMeterConfiguration;
import com.github.liaomengge.base_common.metric.datasource.hikari.HikariMeterConfiguration;
import com.github.liaomengge.base_common.metric.http.httpclient.HttpClientMeterConfiguration;
import com.github.liaomengge.base_common.metric.http.okhttp3.Okhttp3MeterConfiguration;
import com.github.liaomengge.base_common.metric.threadpool.ThreadPoolMetricsConfiguration;
import com.github.liaomengge.base_common.metric.mq.activemq.ActiveMQMeterConfiguration;
import com.github.liaomengge.base_common.metric.web.tomcat.TomcatMeterConfiguration;
import com.github.liaomengge.base_common.metric.web.undertow.UndertowMeterConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Configuration
@EnableConfigurationProperties(MetricProperties.class)
@Import({LocalCacheMeterConfiguration.class, RedisCacheMeterConfiguration.class,
        DruidMeterConfiguration.class, HikariMeterConfiguration.class,
        HttpClientMeterConfiguration.class, Okhttp3MeterConfiguration.class,
        ActiveMQMeterConfiguration.class, ThreadPoolMetricsConfiguration.class})
@ImportAutoConfiguration({TomcatMeterConfiguration.class, UndertowMeterConfiguration.class})
public class MetricAutoConfiguration {
}
