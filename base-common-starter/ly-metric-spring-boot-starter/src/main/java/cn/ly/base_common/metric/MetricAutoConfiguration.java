package cn.ly.base_common.metric;

import cn.ly.base_common.metric.cache.local.LocalCacheMetricsConfiguration;
import cn.ly.base_common.metric.cache.redis.RedisCacheMetricsConfiguration;
import cn.ly.base_common.metric.datasource.druid.DruidMetricsConfiguration;
import cn.ly.base_common.metric.datasource.hikari.HikariMetricsConfiguration;
import cn.ly.base_common.metric.http.httpclient.HttpClientMetricsConfiguration;
import cn.ly.base_common.metric.http.okhttp3.Okhttp3MetricsConfiguration;
import cn.ly.base_common.metric.threadpool.ThreadPoolMetricsConfiguration;
import cn.ly.base_common.metric.web.tomcat.TomcatMetricsConfiguration;
import cn.ly.base_common.metric.web.undertow.UndertowMetricsConfiguration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Configuration
@EnableConfigurationProperties(MetricProperties.class)
@Import({LocalCacheMetricsConfiguration.class, RedisCacheMetricsConfiguration.class,
        DruidMetricsConfiguration.class, HikariMetricsConfiguration.class,
        HttpClientMetricsConfiguration.class, Okhttp3MetricsConfiguration.class,
        ThreadPoolMetricsConfiguration.class})
@ImportAutoConfiguration({TomcatMetricsConfiguration.class, UndertowMetricsConfiguration.class})
public class MetricAutoConfiguration {
}
