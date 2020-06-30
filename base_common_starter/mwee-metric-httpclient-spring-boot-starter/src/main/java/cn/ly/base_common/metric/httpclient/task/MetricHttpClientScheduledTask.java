package cn.ly.base_common.metric.httpclient.task;

/**
 * Created by liaomengge on 2019/7/30.
 */

import cn.ly.base_common.metric.httpclient.MetricHttpClientProperties;
import cn.mwee.base_common.utils.collection.MwCollectionUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.thread.MwThreadUtil;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class MetricHttpClientScheduledTask {

    private static final Logger logger = MwLogger.getInstance(MetricHttpClientScheduledTask.class);

    private static final String METRIC_HTTPCLIENT_PREFIX = "metric-http-client.";

    private StatsDClient statsDClient;
    private MetricHttpClientProperties metricHttpClientProperties;
    private PoolingHttpClientConnectionManager poolConnManager;

    @PostConstruct
    private void init() {
        HttpClientStatsThread httpClientStatsThread = new HttpClientStatsThread("metric-http-client-stats");
        httpClientStatsThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(httpClientStatsThread::interrupt));
    }

    private class HttpClientStatsThread extends Thread {

        public HttpClientStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            long initialDelay = metricHttpClientProperties.getInitialDelay() * 1000L;
            if (initialDelay > 0) {
                MwThreadUtil.sleep(initialDelay);
            }
            while (!this.isInterrupted()) {
                try {
                    if (Objects.nonNull(poolConnManager)) {
                        PoolStats poolStats = poolConnManager.getTotalStats();
                        if (Objects.nonNull(statsDClient)) {
                            String totalPrefix = METRIC_HTTPCLIENT_PREFIX + "total.";
                            statsPool(totalPrefix, poolStats);
                        }

                        Set<HttpRoute> httpRoutes = poolConnManager.getRoutes();
                        if (CollectionUtils.isNotEmpty(httpRoutes)) {
                            Comparator<HttpRoute> comparator =
                                    Comparator.<HttpRoute>comparingInt(val -> poolConnManager.getStats(val).getPending())
                                            .thenComparingInt(val -> poolConnManager.getStats(val).getLeased())
                                            .thenComparingInt(val -> poolConnManager.getStats(val).getAvailable());
                            List<HttpRoute> httpRouteList = MwCollectionUtil.topN(httpRoutes,
                                    Math.min(httpRoutes.size(), metricHttpClientProperties.getMaxHttpRoueCount()),
                                    comparator);
                            httpRouteList.forEach(val -> {
                                PoolStats routePoolStats = poolConnManager.getStats(val);
                                String routePrefix =
                                        METRIC_HTTPCLIENT_PREFIX + "route." + StringUtils.replaceChars(val.getTargetHost().getHostName(), '.', '_') + '.';
                                statsPool(routePrefix, routePoolStats);
                            });
                        }
                    }

                    TimeUnit.SECONDS.sleep(metricHttpClientProperties.getStatsInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("metric http client interrupt exit...");
                } catch (Exception e) {
                    logger.error("metric http client exception...", e);
                }
            }
        }
    }

    private void statsPool(String prefix, PoolStats poolStats) {
        if (Objects.nonNull(statsDClient)) {
            statsDClient.recordExecutionTime(prefix + "leased", poolStats.getLeased());
            statsDClient.recordExecutionTime(prefix + "pending", poolStats.getPending());
            statsDClient.recordExecutionTime(prefix + "available", poolStats.getAvailable());
            statsDClient.recordExecutionTime(prefix + "max", poolStats.getMax());
            return;
        }
        StringBuilder sBuilder = new StringBuilder(16);
        sBuilder.append(prefix + "leased => [" + poolStats.getLeased() + "],");
        sBuilder.append(prefix + "pending => [" + poolStats.getPending() + "],");
        sBuilder.append(prefix + "available => [" + poolStats.getAvailable() + "],");
        sBuilder.append(prefix + "max => [" + poolStats.getMax() + "]");
        logger.info(sBuilder.toString());
    }
}
