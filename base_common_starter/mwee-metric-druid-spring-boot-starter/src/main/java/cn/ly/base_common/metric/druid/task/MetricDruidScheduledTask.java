package cn.ly.base_common.metric.druid.task;

import cn.ly.base_common.metric.druid.MetricDruidProperties;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.thread.MwThreadUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/7/24.
 */
@AllArgsConstructor
public class MetricDruidScheduledTask {

    private static final Logger logger = MwLogger.getInstance(MetricDruidScheduledTask.class);

    private static final String METRIC_DRUID_PREFIX = "metric-druid.";

    private StatsDClient statsDClient;
    private MetricDruidProperties metricDruidProperties;

    @PostConstruct
    private void init() {
        DruidStatsThread druidStatsThread = new DruidStatsThread("metric-druid-stats");
        druidStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(druidStatsThread::interrupt));
    }

    private class DruidStatsThread extends Thread {

        public DruidStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricDruidProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                MwThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    try {
                        Set<DruidDataSource> druidDataSources =
                                DruidDataSourceStatManager.getDruidDataSourceInstances();
                        Optional.ofNullable(druidDataSources).ifPresent(val -> val.forEach(druidDataSource -> {
                            DruidDataSourceStatValue statValue = druidDataSource.getStatValueAndReset();
                            long maxWaitMillis = druidDataSource.getMaxWait();//最大等待时间
                            long waitThreadCount = statValue.getWaitThreadCount();//当前等待获取连接的线程数
                            long notEmptyWaitMillis = statValue.getNotEmptyWaitMillis();//获取连接时累计等待多长时间
                            long notEmptyWaitCount = statValue.getNotEmptyWaitCount();//获取连接时累计等待多少次'

                            int maxActive = druidDataSource.getMaxActive();//最大活跃数
                            int poolingCount = statValue.getPoolingCount();//当前连接池数
                            int poolingPeak = statValue.getPoolingPeak();//连接池峰值
                            int activeCount = statValue.getActiveCount();//当前活跃连接数
                            int activePeak = statValue.getActivePeak();//活跃数峰值

                            if (Objects.nonNull(statsDClient)) {
                                URI jdbcUri = parseJdbcUrl(druidDataSource.getUrl());
                                Optional.ofNullable(jdbcUri).ifPresent(val2 -> {
                                    String host = StringUtils.replaceChars(val2.getHost(), '.', '_');
                                    String prefix = METRIC_DRUID_PREFIX + host + '.' + val2.getPort() + '.';
                                    statsDClient.recordExecutionTime(prefix + "maxWaitMillis", maxWaitMillis);
                                    statsDClient.recordExecutionTime(prefix + "waitThreadCount", waitThreadCount);
                                    statsDClient.recordExecutionTime(prefix + "notEmptyWaitMillis", notEmptyWaitMillis);
                                    statsDClient.recordExecutionTime(prefix + "notEmptyWaitCount", notEmptyWaitCount);
                                    statsDClient.recordExecutionTime(prefix + "maxActive", maxActive);
                                    statsDClient.recordExecutionTime(prefix + "poolingCount", poolingCount);
                                    statsDClient.recordExecutionTime(prefix + "poolingPeak", poolingPeak);
                                    statsDClient.recordExecutionTime(prefix + "activeCount", activeCount);
                                    statsDClient.recordExecutionTime(prefix + "activePeak", activePeak);
                                });
                            } else {
                                druidDataSource.logStats();
                            }
                        }));
                    } catch (Exception e) {
                        logger.error("druid stats exception", e);
                    }
                    TimeUnit.SECONDS.sleep(metricDruidProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("metric druid interrupt exit...");
                } catch (Exception e) {
                    logger.error("metric druid exception...", e);
                }
            }
        }
    }

    private URI parseJdbcUrl(String url) {
        if (StringUtils.isBlank(url) || !StringUtils.startsWith(url, "jdbc:")) {
            return null;
        }
        String cleanURI = url.substring(5);
        return URI.create(cleanURI);
    }
}
