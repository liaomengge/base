package cn.ly.base_common.metric;

import cn.ly.base_common.metric.metrics.thread.ThreadStatePublicMetrics;
import cn.ly.base_common.metric.metrics.thread.custom.ThreadPoolMetricsConfiguration;
import cn.ly.base_common.metric.metrics.thread.custom.ThreadPoolPublicMetrics;
import cn.ly.base_common.metric.metrics.thread.tomcat.TomcatMetricsConfiguration;
import cn.ly.base_common.metric.metrics.thread.tomcat.TomcatPublicMetrics;
import cn.ly.base_common.metric.task.MetricScheduledTask;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.statsd.StatsdMetricWriter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({StatsdMetricWriter.class, StatsDClient.class})
@EnableConfigurationProperties(MetricProperties.class)
@ConditionalOnProperty(prefix = "ly.metric", name = "enabled", matchIfMissing = true)
@Import({TomcatMetricsConfiguration.class, ThreadPoolMetricsConfiguration.class})
public class MetricAutoConfiguration {

    @Autowired
    private MetricProperties metricProperties;

    private final TomcatPublicMetrics tomcatPublicMetrics;

    public MetricAutoConfiguration(ObjectProvider<TomcatPublicMetrics> objectProvider) {
        this.tomcatPublicMetrics = objectProvider.getIfUnique();
    }

    @Bean
    @ConditionalOnMissingBean
    public StatsDClient statsDClient() {
        MetricProperties.StatsdProperties statsdProperties = this.metricProperties.getStatsd();
        return new NonBlockingStatsDClient(this.trimPrefix(statsdProperties.getPrefix()),
                statsdProperties.getHostname(), statsdProperties.getPort());
    }

    private String trimPrefix(String prefix) {
        String trimmedPrefix = (StringUtils.hasText(prefix) ? prefix : null);
        while (trimmedPrefix != null && trimmedPrefix.endsWith(".")) {
            trimmedPrefix = trimmedPrefix.substring(0, trimmedPrefix.length() - 1);
        }

        return trimmedPrefix;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StatsDClient.class)
    public StatsdMetricWriter statsdMetricWriter(StatsDClient statsDClient) {
        return new StatsdMetricWriter(statsDClient) {
            @Override
            public void set(Metric<?> value) {
                String name = value.getName().replace(":", "-");
                statsDClient.recordExecutionTime(name, value.getValue().longValue());
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SystemPublicMetrics systemPublicMetrics() {
        return new SystemPublicMetrics();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadStatePublicMetrics threadStatePublicMetrics() {
        return new ThreadStatePublicMetrics();
    }

    @Bean
    @ConditionalOnBean({StatsdMetricWriter.class, SystemPublicMetrics.class,
            ThreadPoolPublicMetrics.class, ThreadStatePublicMetrics.class})
    public MetricScheduledTask metricScheduledTask(StatsdMetricWriter statsdMetricWriter,
                                                   SystemPublicMetrics systemPublicMetrics,
                                                   ThreadPoolPublicMetrics threadPoolPublicMetrics,
                                                   ThreadStatePublicMetrics threadStatePublicMetrics) {
        MetricScheduledTask metricScheduledTask = new MetricScheduledTask(metricProperties, statsdMetricWriter,
                systemPublicMetrics, threadPoolPublicMetrics, threadStatePublicMetrics);
        metricScheduledTask.setTomcatPublicMetrics(this.tomcatPublicMetrics);
        return metricScheduledTask;
    }
}
