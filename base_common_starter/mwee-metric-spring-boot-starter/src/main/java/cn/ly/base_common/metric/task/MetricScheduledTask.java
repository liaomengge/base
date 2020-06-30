package cn.ly.base_common.metric.task;

import cn.ly.base_common.metric.MetricProperties;
import cn.ly.base_common.metric.metrics.thread.MwThreadStatePublicMetrics;
import cn.ly.base_common.metric.metrics.thread.custom.MwThreadPoolPublicMetrics;
import cn.ly.base_common.metric.metrics.thread.tomcat.MwTomcatPublicMetrics;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.shutdown.MwShutdownUtil;
import cn.mwee.base_common.utils.thread.MwRuntimeUtil;
import cn.mwee.base_common.utils.thread.MwThreadFactoryBuilderUtil;
import lombok.Data;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.statsd.StatsdMetricWriter;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Data
public class MetricScheduledTask {

    private static final Logger logger = MwLogger.getInstance(MetricScheduledTask.class);

    private static final String METRIC_PREFIX = "metric.";

    private final MetricProperties metricProperties;
    private final StatsdMetricWriter statsdMetricWriter;
    private final SystemPublicMetrics systemPublicMetrics;
    private final MwThreadPoolPublicMetrics mwThreadPoolPublicMetrics;
    private final MwThreadStatePublicMetrics mwThreadStatePublicMetrics;

    @Setter
    private MwTomcatPublicMetrics mwTomcatPublicMetrics;

    public MetricScheduledTask(MetricProperties metricProperties,
                               StatsdMetricWriter statsdMetricWriter,
                               SystemPublicMetrics systemPublicMetrics,
                               MwThreadPoolPublicMetrics mwThreadPoolPublicMetrics,
                               MwThreadStatePublicMetrics mwThreadStatePublicMetrics) {
        this.metricProperties = metricProperties;
        this.statsdMetricWriter = statsdMetricWriter;
        this.systemPublicMetrics = systemPublicMetrics;
        this.mwThreadPoolPublicMetrics = mwThreadPoolPublicMetrics;
        this.mwThreadStatePublicMetrics = mwThreadStatePublicMetrics;
    }

    @PostConstruct
    private void init() {
        ScheduledThreadPoolExecutor poolExecutor =
                new ScheduledThreadPoolExecutor(MwRuntimeUtil.getCpuNum(),
                        MwThreadFactoryBuilderUtil.build("metric"), new ThreadPoolExecutor.CallerRunsPolicy());
        poolExecutor.scheduleAtFixedRate(this::metric, metricProperties.getInitialDelay(), metricProperties.getPeriod(),
                TimeUnit.SECONDS);
        this.registerShutdownHook(poolExecutor);
    }

    private void metric() {
        metricJvm();
        metricTomcat();
        metricThreadPool();
    }

    /**
     * metric Jvm
     */
    private void metricJvm() {
        Collection<Metric<?>> metrics = this.systemPublicMetrics.metrics();
        metrics.addAll(this.mwThreadStatePublicMetrics.metrics());
        metrics.forEach(val -> {
            String name = METRIC_PREFIX + val.getName();
            Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
            this.statsdMetricWriter.set(metric);
        });
    }

    /**
     * metric tomcat
     */
    private void metricTomcat() {
        if (Objects.nonNull(this.mwTomcatPublicMetrics)) {
            Collection<Metric<?>> metrics = this.mwTomcatPublicMetrics.metrics();
            metrics.forEach(val -> {
                String name = METRIC_PREFIX + val.getName();
                Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
                this.statsdMetricWriter.set(metric);
            });
        }
    }

    /**
     * metric thread pool
     */
    private void metricThreadPool() {
        Collection<Metric<?>> metrics = this.mwThreadPoolPublicMetrics.metrics();
        metrics.forEach(val -> {
            String name = METRIC_PREFIX + val.getName();
            Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
            this.statsdMetricWriter.set(metric);
        });
    }

    /**
     * 程序退出时的回调勾子
     * @param poolExecutor
     */
    private void registerShutdownHook(ScheduledThreadPoolExecutor poolExecutor) {
        MwShutdownUtil.registerShutdownHook(() -> {
            try {
                logger.info("Metric Scheduled Thread Pool Exist...");
            } finally {
                if (poolExecutor != null) {
                    poolExecutor.shutdown();
                }
            }
        });
    }
}
