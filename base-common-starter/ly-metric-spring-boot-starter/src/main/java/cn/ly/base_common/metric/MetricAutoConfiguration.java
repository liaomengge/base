package cn.ly.base_common.metric;

import cn.ly.base_common.metric.cache.local.LocalCacheMeterConfiguration;
import cn.ly.base_common.metric.cache.redis.RedisCacheMeterConfiguration;
import cn.ly.base_common.metric.datasource.druid.DruidMeterConfiguration;
import cn.ly.base_common.metric.datasource.hikari.HikariMeterConfiguration;
import cn.ly.base_common.metric.http.httpclient.HttpClientMeterConfiguration;
import cn.ly.base_common.metric.http.okhttp3.Okhttp3MeterConfiguration;
import cn.ly.base_common.metric.threadpool.ThreadPoolMetricsConfiguration;
import cn.ly.base_common.metric.web.tomcat.TomcatMeterConfiguration;
import cn.ly.base_common.metric.web.undertow.UndertowMeterConfiguration;
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
        ThreadPoolMetricsConfiguration.class})
@ImportAutoConfiguration({TomcatMeterConfiguration.class, UndertowMeterConfiguration.class})
public class MetricAutoConfiguration {
}
