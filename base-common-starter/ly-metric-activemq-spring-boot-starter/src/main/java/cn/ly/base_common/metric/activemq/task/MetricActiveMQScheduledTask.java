package cn.ly.base_common.metric.activemq.task;

/**
 * Created by liaomengge on 2019/7/30.
 */

import cn.ly.base_common.metric.activemq.MetricActiveMQProperties;
import cn.ly.base_common.mq.activemq.pool.MonitorPooledConnectionFactory;
import cn.ly.base_common.mq.activemq.pool.MonitorPooledConnectionFactory.PoolMonitor;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadUtil;

import com.timgroup.statsd.StatsDClient;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

public class MetricActiveMQScheduledTask {

    private static final Logger log = LyLogger.getInstance(MetricActiveMQScheduledTask.class);

    private static final String METRIC_REDIS_PREFIX = "metric-activemq.";

    private StatsDClient statsDClient;
    private PooledConnectionFactory pooledConnectionFactory;
    private MetricActiveMQProperties metricActiveMQProperties;

    public MetricActiveMQScheduledTask(StatsDClient statsDClient, PooledConnectionFactory pooledConnectionFactory,
                                       MetricActiveMQProperties metricActiveMQProperties) {
        this.statsDClient = statsDClient;
        this.pooledConnectionFactory = pooledConnectionFactory;
        this.metricActiveMQProperties = metricActiveMQProperties;
    }

    @PostConstruct
    private void init() {
        ActiveMQStatsThread activeMQStatsThread = new ActiveMQStatsThread("metric-activemq-stats");
        activeMQStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(activeMQStatsThread::interrupt));
    }

    private class ActiveMQStatsThread extends Thread {

        public ActiveMQStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricActiveMQProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                LyThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    Optional.ofNullable(pooledConnectionFactory).ifPresent(val -> {
                        if (val instanceof MonitorPooledConnectionFactory) {
                            MonitorPooledConnectionFactory connectionFactory = (MonitorPooledConnectionFactory) val;
                            PoolMonitor poolMonitor = connectionFactory.createPoolMonitor();
                            statsPool(METRIC_REDIS_PREFIX, poolMonitor);
                        }
                    });
                    TimeUnit.SECONDS.sleep(metricActiveMQProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("metric activemq interrupt exit...");
                } catch (Exception e) {
                    log.error("metric activemq exception...", e);
                }
            }
        }
    }

    private void statsPool(String prefix, PoolMonitor poolMonitor) {
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(prefix + "maxTotal", poolMonitor.getMaxTotal());
            statsDClient.recordExecutionTime(prefix + "numActive", poolMonitor.getNumActive());
            statsDClient.recordExecutionTime(prefix + "numIdle", poolMonitor.getNumIdle());
            statsDClient.recordExecutionTime(prefix + "numWaiters", poolMonitor.getNumWaiters());
            statsDClient.recordExecutionTime(prefix + "maxWaitMillis", poolMonitor.getMaxWaitMillis());
            return;
        }
        StringBuilder sBuilder = new StringBuilder(16);
        sBuilder.append(prefix + "maxTotal => [" + poolMonitor.getMaxTotal() + "],");
        sBuilder.append(prefix + "numActive => [" + poolMonitor.getNumActive() + "],");
        sBuilder.append(prefix + "numIdle => [" + poolMonitor.getNumIdle() + "],");
        sBuilder.append(prefix + "numWaiters => [" + poolMonitor.getMaxWaitMillis() + "],");
        sBuilder.append(prefix + "maxWaitMillis => [" + poolMonitor.getMaxWaitMillis() + "]");
        log.info(sBuilder.toString());
    }
}
