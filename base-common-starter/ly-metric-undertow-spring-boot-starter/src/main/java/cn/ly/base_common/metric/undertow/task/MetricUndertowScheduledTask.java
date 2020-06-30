package cn.ly.base_common.metric.undertow.task;

/**
 * Created by liaomengge on 2019/7/30.
 */

import cn.ly.base_common.metric.undertow.MetricUndertowProperties;
import cn.ly.base_common.utils.log4j2.MwLogger;
import cn.ly.base_common.utils.thread.MwThreadUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class MetricUndertowScheduledTask {

    private static final Logger logger = MwLogger.getInstance(MetricUndertowScheduledTask.class);

    private static final String METRIC_HTTPCLIENT_PREFIX = "metric-undertow.";
    private static final String JMX_NAME_BASE = "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\"";

    private StatsDClient statsDClient;
    private MetricUndertowProperties metricUndertowProperties;

    private MBeanServer mbeanServer;

    public MetricUndertowScheduledTask(StatsDClient statsDClient, MetricUndertowProperties metricUndertowProperties) {
        this.statsDClient = statsDClient;
        this.metricUndertowProperties = metricUndertowProperties;
    }

    @PostConstruct
    private void init() {
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        UndertowStatsThread undertowStatsThread = new UndertowStatsThread("metric-undertow-stats");
        undertowStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(undertowStatsThread::interrupt));
    }

    private class UndertowStatsThread extends Thread {

        public UndertowStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricUndertowProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                MwThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    if (Objects.nonNull(mbeanServer)) {
                        ObjectName objectName = new ObjectName(JMX_NAME_BASE);
                        PoolStatBean poolStatBean = buildPoolStatBean(objectName);
                        statsPool(poolStatBean, "undertow");
                    }
                    TimeUnit.SECONDS.sleep(metricUndertowProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("metric undertow interrupt exit...");
                } catch (Exception e) {
                    logger.error("metric undertow exception...", e);
                }
            }
        }
    }

    private PoolStatBean buildPoolStatBean(ObjectName objectName) throws Exception {
        int busyWorkerThreadCount = (int) mbeanServer.getAttribute(objectName, "BusyWorkerThreadCount");
        int workerQueueSize = (int) mbeanServer.getAttribute(objectName, "WorkerQueueSize");
        int coreWorkerPoolSize = (int) mbeanServer.getAttribute(objectName, "CoreWorkerPoolSize");
        int maxWorkerPoolSize = (int) mbeanServer.getAttribute(objectName, "MaxWorkerPoolSize");
        return PoolStatBean.builder()
                .busyWorkerThreadCount(busyWorkerThreadCount)
                .workerQueueSize(workerQueueSize)
                .coreWorkerPoolSize(coreWorkerPoolSize)
                .maxWorkerPoolSize(maxWorkerPoolSize)
                .build();
    }

    private void statsPool(PoolStatBean poolStatBean, String jmxNamePrefix) {
        String metricPrefix = METRIC_HTTPCLIENT_PREFIX + jmxNamePrefix + '.';
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(metricPrefix + "busyWorkerThreadCount",
                    poolStatBean.getBusyWorkerThreadCount());
            statsDClient.recordExecutionTime(metricPrefix + "workerQueueSize", poolStatBean.getWorkerQueueSize());
            statsDClient.recordExecutionTime(metricPrefix + "coreWorkerPoolSize", poolStatBean.getCoreWorkerPoolSize());
            statsDClient.recordExecutionTime(metricPrefix + "maxWorkerPoolSize", poolStatBean.getMaxWorkerPoolSize());
            return;
        }
        StringBuilder sBuilder = new StringBuilder(4);
        sBuilder.append(metricPrefix + "busyWorkerThreadCount => [" + poolStatBean.getBusyWorkerThreadCount() + "],");
        sBuilder.append(metricPrefix + "workerQueueSize => [" + poolStatBean.getWorkerQueueSize() + "],");
        sBuilder.append(metricPrefix + "coreWorkerPoolSize => [" + poolStatBean.getCoreWorkerPoolSize() + "],");
        sBuilder.append(metricPrefix + "maxWorkerPoolSize => [" + poolStatBean.getMaxWorkerPoolSize() + "]");
        logger.info(sBuilder.toString());
    }

    @Data
    @Builder
    private static class PoolStatBean {
        private int busyWorkerThreadCount;
        private int workerQueueSize;
        private int coreWorkerPoolSize;
        private int maxWorkerPoolSize;
    }
}
