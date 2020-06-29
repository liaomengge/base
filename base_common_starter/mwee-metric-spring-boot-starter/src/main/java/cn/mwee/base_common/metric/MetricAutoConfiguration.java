package cn.mwee.base_common.metric;

import cn.mwee.base_common.metric.metrics.thread.MwThreadStatePublicMetrics;
import cn.mwee.base_common.metric.metrics.thread.custom.MwThreadPoolMetricsConfiguration;
import cn.mwee.base_common.metric.metrics.thread.custom.MwThreadPoolPublicMetrics;
import cn.mwee.base_common.metric.metrics.thread.tomcat.MwTomcatMetricsConfiguration;
import cn.mwee.base_common.metric.metrics.thread.tomcat.MwTomcatPublicMetrics;
import cn.mwee.base_common.metric.task.MetricScheduledTask;
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
@ConditionalOnProperty(prefix = "mwee.metric", name = "enabled", matchIfMissing = true)
@Import({MwTomcatMetricsConfiguration.class, MwThreadPoolMetricsConfiguration.class})
public class MetricAutoConfiguration {

    @Autowired
    private MetricProperties metricProperties;

    private final MwTomcatPublicMetrics mwTomcatPublicMetrics;

    public MetricAutoConfiguration(ObjectProvider<MwTomcatPublicMetrics> objectProvider) {
        this.mwTomcatPublicMetrics = objectProvider.getIfUnique();
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
    public MwThreadStatePublicMetrics mwThreadStatePublicMetrics() {
        return new MwThreadStatePublicMetrics();
    }

    @Bean
    @ConditionalOnBean({StatsdMetricWriter.class, SystemPublicMetrics.class,
            MwThreadPoolPublicMetrics.class, MwThreadStatePublicMetrics.class})
    public MetricScheduledTask metricScheduledTask(StatsdMetricWriter statsdMetricWriter,
                                                   SystemPublicMetrics systemPublicMetrics,
                                                   MwThreadPoolPublicMetrics mwThreadPoolPublicMetrics,
                                                   MwThreadStatePublicMetrics mwThreadStatePublicMetrics) {
        MetricScheduledTask metricScheduledTask = new MetricScheduledTask(metricProperties, statsdMetricWriter,
                systemPublicMetrics, mwThreadPoolPublicMetrics, mwThreadStatePublicMetrics);
        metricScheduledTask.setMwTomcatPublicMetrics(this.mwTomcatPublicMetrics);
        return metricScheduledTask;
    }
}
