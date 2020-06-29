package cn.mwee.base_common.metric.hikari.task;

import cn.mwee.base_common.metric.hikari.MetricHikariProperties;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.thread.MwThreadUtil;
import com.timgroup.statsd.StatsDClient;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/7/24.
 */
@AllArgsConstructor
public class MetricHikariScheduledTask {

    private static final Logger logger = MwLogger.getInstance(MetricHikariScheduledTask.class);

    private static final String METRIC_HIKARI_PREFIX = "metric-hikari.";

    private StatsDClient statsDClient;
    private MetricHikariProperties metricHikariProperties;
    private List<HikariDataSource> hikariDataSources;

    @PostConstruct
    private void init() {
        HikariStatsThread hikariStatsThread = new HikariStatsThread("metric-hikari-stats");
        hikariStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(hikariStatsThread::interrupt));
    }

    private class HikariStatsThread extends Thread {

        public HikariStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricHikariProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                MwThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    Optional.ofNullable(hikariDataSources).ifPresent(val -> val.forEach(hikariDataSource -> {
                        URI jdbcUri = parseJdbcUrl(hikariDataSource.getJdbcUrl());
                        Optional.ofNullable(jdbcUri).ifPresent(val2 -> {
                            String host = StringUtils.replaceChars(val2.getHost(), '.', '_');
                            String prefix = METRIC_HIKARI_PREFIX + host + '.' + val2.getPort() + '.';

                            PoolStatBean poolStatBean = PoolStatBean.builder().build();
                            HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();
                            Optional.ofNullable(hikariPoolMXBean).ifPresent(val3 -> {
                                int activeConnections = val3.getActiveConnections();
                                int idleConnections = val3.getIdleConnections();
                                int totalConnections = val3.getTotalConnections();
                                int threadsAwaitingConnection = val3.getThreadsAwaitingConnection();
                                poolStatBean.setActiveConnections(activeConnections);
                                poolStatBean.setIdleConnections(idleConnections);
                                poolStatBean.setTotalConnections(totalConnections);
                                poolStatBean.setThreadsAwaitingConnection(threadsAwaitingConnection);
                            });
                            HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
                            Optional.ofNullable(hikariConfigMXBean).ifPresent(val3 -> {
                                int maximumPoolSize = val3.getMaximumPoolSize();
                                int minimumIdle = val3.getMinimumIdle();
                                poolStatBean.setMaximumPoolSize(maximumPoolSize);
                                poolStatBean.setMinimumIdle(minimumIdle);
                            });
                            statsPool(prefix, poolStatBean);
                        });
                    }));
                    TimeUnit.SECONDS.sleep(metricHikariProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("metric hikari interrupt exit...");
                } catch (Exception e) {
                    logger.error("metric hikari exception...", e);
                }
            }
        }
    }

    private void statsPool(String prefix, PoolStatBean poolStatBean) {
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(prefix + "activeConnections", poolStatBean.getActiveConnections());
            statsDClient.recordExecutionTime(prefix + "idleConnections", poolStatBean.getIdleConnections());
            statsDClient.recordExecutionTime(prefix + "totalConnections", poolStatBean.getTotalConnections());
            statsDClient.recordExecutionTime(prefix + "threadsAwaitingConnection",
                    poolStatBean.getThreadsAwaitingConnection());
            statsDClient.recordExecutionTime(prefix + "maximumPoolSize", poolStatBean.getMaximumPoolSize());
            statsDClient.recordExecutionTime(prefix + "minimumIdle", poolStatBean.getMinimumIdle());
            return;
        }
        StringBuilder sBuilder = new StringBuilder(16);
        sBuilder.append(prefix + "activeConnections => [" + poolStatBean.getActiveConnections() + "],");
        sBuilder.append(prefix + "idleConnections => [" + poolStatBean.getIdleConnections() + "],");
        sBuilder.append(prefix + "totalConnections => [" + poolStatBean.getTotalConnections() + "],");
        sBuilder.append(prefix + "threadsAwaitingConnection => [" + poolStatBean.getThreadsAwaitingConnection() + "],");
        sBuilder.append(prefix + "maximumPoolSize => [" + poolStatBean.getMaximumPoolSize() + "],");
        sBuilder.append(prefix + "minimumIdle => [" + poolStatBean.getMinimumIdle() + "]");
        logger.info(sBuilder.toString());
    }

    private URI parseJdbcUrl(String url) {
        if (StringUtils.isBlank(url) || !StringUtils.startsWith(url, "jdbc:")) {
            return null;
        }
        String cleanURI = url.substring(5);
        return URI.create(cleanURI);
    }

    @Data
    @Builder
    private static class PoolStatBean {
        private int activeConnections;
        private int idleConnections;
        private int totalConnections;
        private int threadsAwaitingConnection;
        private int maximumPoolSize;
        private int minimumIdle;
    }
}
