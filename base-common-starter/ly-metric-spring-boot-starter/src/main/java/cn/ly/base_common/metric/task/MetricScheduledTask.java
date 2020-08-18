//package cn.ly.base_common.metric.task;
//
//import cn.ly.base_common.metric.MetricProperties;
//import cn.ly.base_common.metric.metrics.thread.ThreadStatePublicMetrics;
//import cn.ly.base_common.metric.metrics.thread.custom.ThreadPoolPublicMetrics;
//import cn.ly.base_common.metric.metrics.thread.tomcat.TomcatPublicMetrics;
//import cn.ly.base_common.utils.log4j2.LyLogger;
//import cn.ly.base_common.utils.shutdown.LyShutdownUtil;
//import cn.ly.base_common.utils.thread.LyRuntimeUtil;
//import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
//import lombok.Data;
//import lombok.Setter;
//import org.slf4j.Logger;
//import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
//import org.springframework.boot.actuate.metrics.Metric;
//import org.springframework.boot.actuate.metrics.statsd.StatsdMetricWriter;
//
//import javax.annotation.PostConstruct;
//import java.util.Collection;
//import java.util.Objects;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by liaomengge on 2018/12/19.
// */
//@Data
//public class MetricScheduledTask {
//
//    private static final Logger log = LyLogger.getInstance(MetricScheduledTask.class);
//
//    private static final String METRIC_PREFIX = "metric.";
//
//    private final MetricProperties metricProperties;
//    private final StatsdMetricWriter statsdMetricWriter;
//    private final SystemPublicMetrics systemPublicMetrics;
//    private final ThreadPoolPublicMetrics threadPoolPublicMetrics;
//    private final ThreadStatePublicMetrics threadStatePublicMetrics;
//
//    @Setter
//    private TomcatPublicMetrics tomcatPublicMetrics;
//
//    public MetricScheduledTask(MetricProperties metricProperties,
//                               StatsdMetricWriter statsdMetricWriter,
//                               SystemPublicMetrics systemPublicMetrics,
//                               ThreadPoolPublicMetrics threadPoolPublicMetrics,
//                               ThreadStatePublicMetrics LyThreadStatePublicMetrics) {
//        this.metricProperties = metricProperties;
//        this.statsdMetricWriter = statsdMetricWriter;
//        this.systemPublicMetrics = systemPublicMetrics;
//        this.threadPoolPublicMetrics = threadPoolPublicMetrics;
//        this.threadStatePublicMetrics = LyThreadStatePublicMetrics;
//    }
//
//    @PostConstruct
//    private void init() {
//        ScheduledThreadPoolExecutor poolExecutor =
//                new ScheduledThreadPoolExecutor(LyRuntimeUtil.getCpuNum(),
//                        LyThreadFactoryBuilderUtil.build("metric"), new ThreadPoolExecutor.CallerRunsPolicy());
//        poolExecutor.scheduleAtFixedRate(this::metric, metricProperties.getInitialDelay(), metricProperties
//        .getPeriod(),
//                TimeUnit.SECONDS);
//        this.registerShutdownHook(poolExecutor);
//    }
//
//    private void metric() {
//        metricJvm();
//        metricTomcat();
//        metricThreadPool();
//    }
//
//    /**
//     * metric Jvm
//     */
//    private void metricJvm() {
//        Collection<Metric<?>> metrics = this.systemPublicMetrics.metrics();
//        metrics.addAll(this.threadStatePublicMetrics.metrics());
//        metrics.forEach(val -> {
//            String name = METRIC_PREFIX + val.getName();
//            Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
//            this.statsdMetricWriter.set(metric);
//        });
//    }
//
//    /**
//     * metric tomcat
//     */
//    private void metricTomcat() {
//        if (Objects.nonNull(this.tomcatPublicMetrics)) {
//            Collection<Metric<?>> metrics = this.tomcatPublicMetrics.metrics();
//            metrics.forEach(val -> {
//                String name = METRIC_PREFIX + val.getName();
//                Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
//                this.statsdMetricWriter.set(metric);
//            });
//        }
//    }
//
//    /**
//     * metric thread pool
//     */
//    private void metricThreadPool() {
//        Collection<Metric<?>> metrics = this.threadPoolPublicMetrics.metrics();
//        metrics.forEach(val -> {
//            String name = METRIC_PREFIX + val.getName();
//            Metric<?> metric = new Metric<>(name, val.getValue(), val.getTimestamp());
//            this.statsdMetricWriter.set(metric);
//        });
//    }
//
//    /**
//     * 程序退出时的回调勾子
//     * @param poolExecutor
//     */
//    private void registerShutdownHook(ScheduledThreadPoolExecutor poolExecutor) {
//        LyShutdownUtil.registerShutdownHook(() -> {
//            try {
//                log.info("Metric Scheduled Thread Pool Exist...");
//            } finally {
//                if (poolExecutor != null) {
//                    poolExecutor.shutdown();
//                }
//            }
//        });
//    }
//}
