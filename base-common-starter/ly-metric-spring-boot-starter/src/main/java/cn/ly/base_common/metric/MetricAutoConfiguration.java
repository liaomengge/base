package cn.ly.base_common.metric;

import cn.ly.base_common.metric.datasource.druid.DruidMetricsConfiguration;
import cn.ly.base_common.metric.datasource.hikari.HikariMetricsConfiguration;
import cn.ly.base_common.metric.http.httpclient.HttpClientMetricsConfiguration;
import cn.ly.base_common.metric.http.okhttp3.Okhttp3MetricsConfiguration;
import cn.ly.base_common.metric.threadpool.ThreadPoolMetricsConfiguration;
import cn.ly.base_common.metric.web.undertow.UndertowMetricsConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/19.
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(MetricProperties.class)
@Import({DruidMetricsConfiguration.class, HikariMetricsConfiguration.class,
        HttpClientMetricsConfiguration.class, Okhttp3MetricsConfiguration.class,
        ThreadPoolMetricsConfiguration.class,
        TomcatMetricsAutoConfiguration.class, UndertowMetricsConfiguration.class})
public class MetricAutoConfiguration {
}
