package cn.ly.base_common.metric.undertow;

import cn.ly.base_common.metric.undertow.task.MetricUndertowScheduledTask;
import com.timgroup.statsd.StatsDClient;
import io.undertow.Undertow;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2019/9/2.
 */
@AllArgsConstructor
@Configuration
@ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
@ConditionalOnProperty(prefix = "ly.metric-undertow", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricUndertowProperties.class)
public class MetricUndertowAutoConfiguration {

    private final MetricUndertowProperties metricUndertowProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricUndertowScheduledTask metricUndertowScheduledTask(StatsDClient statsDClient) {
        return new MetricUndertowScheduledTask(statsDClient, metricUndertowProperties);
    }

}
