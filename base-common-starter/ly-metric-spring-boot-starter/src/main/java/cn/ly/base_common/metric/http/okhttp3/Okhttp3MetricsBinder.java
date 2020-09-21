package cn.ly.base_common.metric.http.okhttp3;

import cn.ly.base_common.utils.log4j2.LyLogger;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

import static cn.ly.base_common.metric.consts.MetricsConst.OKHTTP3_PREFIX;

/**
 * Created by liaomengge on 2020/9/17.
 */
public class Okhttp3MetricsBinder implements MeterBinder {

    private static final Logger log = LyLogger.getInstance(Okhttp3MetricsBinder.class);

    private final Iterable<Tag> tags;
    private final OkHttpClient okHttpClient;

    public Okhttp3MetricsBinder(OkHttpClient okHttpClient) {
        this(Collections.emptyList(), okHttpClient);
    }

    public Okhttp3MetricsBinder(Iterable<Tag> tags, OkHttpClient okHttpClient) {
        this.tags = tags;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            registerMetrics(registry);
        } catch (Exception e) {
            log.error("metric okhttp3 error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry) {
        if (Objects.nonNull(okHttpClient)) {
            ConnectionPool connectionPool = okHttpClient.connectionPool();
            if (Objects.nonNull(connectionPool)) {
                bindGauge(registry, OKHTTP3_PREFIX + "connection.count", connectionPool,
                        ConnectionPool::connectionCount);
                bindGauge(registry, OKHTTP3_PREFIX + "idle.connection.count", connectionPool,
                        ConnectionPool::idleConnectionCount);
            }
        }
    }

    private void bindGauge(MeterRegistry registry, String name, ConnectionPool connectionPool,
                           ToDoubleFunction<ConnectionPool> function) {
        Gauge.builder(name, connectionPool, function).tags(tags).register(registry);
    }
}