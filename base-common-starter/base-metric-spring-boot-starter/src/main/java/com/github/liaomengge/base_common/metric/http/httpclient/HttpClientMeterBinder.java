package com.github.liaomengge.base_common.metric.http.httpclient;

import com.github.liaomengge.base_common.metric.MetricProperties;
import com.github.liaomengge.base_common.metric.consts.MetricsConst;
import com.github.liaomengge.base_common.utils.collection.LyCollectionUtil;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;

import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * Created by liaomengge on 2020/9/17.
 */
@Slf4j
public class HttpClientMeterBinder implements MeterBinder {
    
    private final Iterable<Tag> tags;
    private final MetricProperties metricProperties;
    private final PoolingHttpClientConnectionManager poolConnManager;

    public HttpClientMeterBinder(PoolingHttpClientConnectionManager poolConnManager) {
        this(new MetricProperties(), poolConnManager);
    }

    public HttpClientMeterBinder(MetricProperties metricProperties,
                                 PoolingHttpClientConnectionManager poolConnManager) {
        this(Collections.emptyList(), metricProperties, poolConnManager);
    }

    public HttpClientMeterBinder(Iterable<Tag> tags, PoolingHttpClientConnectionManager poolConnManager) {
        this(tags, new MetricProperties(), poolConnManager);
    }

    public HttpClientMeterBinder(Iterable<Tag> tags, MetricProperties metricProperties,
                                 PoolingHttpClientConnectionManager poolConnManager) {
        this.tags = tags;
        this.metricProperties = metricProperties;
        this.poolConnManager = poolConnManager;
    }

    public static void monitor(MeterRegistry registry, CloseableHttpClient httpClient) {
        monitor(registry, Collections.emptyList(),
                (PoolingHttpClientConnectionManager) httpClient.getConnectionManager());
    }

    public static void monitor(MeterRegistry registry, PoolingHttpClientConnectionManager poolConnManager) {
        monitor(registry, Collections.emptyList(), poolConnManager);
    }

    public static void monitor(MeterRegistry registry, Iterable<Tag> tags,
                               PoolingHttpClientConnectionManager poolConnManager) {
        new HttpClientMeterBinder(tags, poolConnManager).bindTo(registry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            registerMetrics(registry);
        } catch (Exception e) {
            log.error("metric httpclient error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry) {
        if (Objects.nonNull(poolConnManager)) {
            PoolStats poolStats = poolConnManager.getTotalStats();
            if (Objects.nonNull(poolStats)) {
                statsPool(registry, "total", poolStats);
            }

            Set<HttpRoute> httpRoutes = poolConnManager.getRoutes();
            if (CollectionUtils.isNotEmpty(httpRoutes)) {
                Comparator<HttpRoute> comparator =
                        Comparator.<HttpRoute>comparingInt(val -> poolConnManager.getStats(val).getPending())
                                .thenComparingInt(val -> poolConnManager.getStats(val).getLeased())
                                .thenComparingInt(val -> poolConnManager.getStats(val).getAvailable());
                List<HttpRoute> httpRouteList = LyCollectionUtil.topN(httpRoutes,
                        Math.min(httpRoutes.size(), metricProperties.getHttp().getHttpclient().getMaxHttpRoueCount())
                        , comparator);

                httpRouteList.forEach(val -> {
                    PoolStats routePoolStats = poolConnManager.getStats(val);
                    String routePrefix = "route." +
                            StringUtils.replaceChars(val.getTargetHost().getHostName(), '.', '_') + '.';
                    statsPool(registry, routePrefix, routePoolStats);
                });
            }
        }
    }

    private void statsPool(MeterRegistry registry, String prefix, PoolStats poolStats) {
        bindGauge(registry, MetricsConst.HTTP_CLIENT_PREFIX + prefix + ".leased", poolStats, PoolStats::getLeased);
        bindGauge(registry, MetricsConst.HTTP_CLIENT_PREFIX + prefix + ".pending", poolStats, PoolStats::getPending);
        bindGauge(registry, MetricsConst.HTTP_CLIENT_PREFIX + prefix + ".available", poolStats,
                PoolStats::getAvailable);
        bindGauge(registry, MetricsConst.HTTP_CLIENT_PREFIX + prefix + ".max", poolStats, PoolStats::getMax);
    }

    private void bindGauge(MeterRegistry registry, String name, PoolStats poolStats,
                           ToDoubleFunction<PoolStats> function) {
        Gauge.builder(name, poolStats, function).tags(tags).register(registry);
    }
}
