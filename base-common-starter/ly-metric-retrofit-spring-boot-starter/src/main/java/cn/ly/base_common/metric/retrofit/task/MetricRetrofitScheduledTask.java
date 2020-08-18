package cn.ly.base_common.metric.retrofit.task;

/**
 * Created by liaomengge on 2019/7/30.
 */

import cn.ly.base_common.metric.retrofit.MetricRetrofitProperties;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class MetricRetrofitScheduledTask {

    private static final Logger log = LyLogger.getInstance(MetricRetrofitScheduledTask.class);

    private static final String METRIC_RETROFIT_PREFIX = "metric-retrofit.";

    private StatsDClient statsDClient;
    private MetricRetrofitProperties metricRetrofitProperties;
    private OkHttpClient okHttpClient;

    @PostConstruct
    private void init() {
        RetrofitStatsThread retrofitStatsThread = new RetrofitStatsThread("metric-retrofit-stats");
        retrofitStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(retrofitStatsThread::interrupt));
    }

    private class RetrofitStatsThread extends Thread {

        public RetrofitStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricRetrofitProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                LyThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    if (Objects.nonNull(okHttpClient)) {
                        ConnectionPool connectionPool = okHttpClient.connectionPool();
                        if (Objects.nonNull(connectionPool)) {
                            int connectionCount = connectionPool.connectionCount();
                            int idleConnectionCount = connectionPool.idleConnectionCount();
                            String connectionCountPrefix = METRIC_RETROFIT_PREFIX + "connectionCount";
                            String idleConnectionCountPrefix = METRIC_RETROFIT_PREFIX + "idleConnectionCount";
                            if (Objects.nonNull(statsDClient)) {
                                statsDClient.recordExecutionTime(connectionCountPrefix, connectionCount);
                                statsDClient.recordExecutionTime(idleConnectionCountPrefix, idleConnectionCount);
                            } else {
                                log.info(connectionCountPrefix + " => [" + connectionCount + "], "
                                        + idleConnectionCountPrefix + " => [" + idleConnectionCount + "]");
                            }
                        }
                    }

                    TimeUnit.SECONDS.sleep(metricRetrofitProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.info("metric retrofit interrupt exit...");
                } catch (Exception e) {
                    log.error("metric retrofit exception...", e);
                }
            }
        }
    }
}
