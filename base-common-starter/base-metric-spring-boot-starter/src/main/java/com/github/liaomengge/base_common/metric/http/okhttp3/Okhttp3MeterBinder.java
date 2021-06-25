package com.github.liaomengge.base_common.metric.http.okhttp3;

import com.github.liaomengge.base_common.metric.consts.MetricsConst;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.Collections;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/**
 * Created by liaomengge on 2020/9/17.
 */
@Slf4j
public class Okhttp3MeterBinder implements MeterBinder {
    
    private final Iterable<Tag> tags;
    private final ConnectionPool connectionPool;

    public Okhttp3MeterBinder(ConnectionPool connectionPool) {
        this(Collections.emptyList(), connectionPool);
    }

    public Okhttp3MeterBinder(Iterable<Tag> tags, ConnectionPool connectionPool) {
        this.tags = tags;
        this.connectionPool = connectionPool;
    }

    public static void monitor(MeterRegistry registry, OkHttpClient okHttpClient) {
        monitor(registry, Collections.emptyList(), okHttpClient.connectionPool());
    }

    public static void monitor(MeterRegistry registry, ConnectionPool connectionPool) {
        monitor(registry, Collections.emptyList(), connectionPool);
    }

    public static void monitor(MeterRegistry registry, Iterable<Tag> tags, ConnectionPool connectionPool) {
        new Okhttp3MeterBinder(tags, connectionPool).bindTo(registry);
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
        if (Objects.nonNull(connectionPool)) {
            bindGauge(registry, MetricsConst.OKHTTP3_PREFIX + "connection.count", connectionPool,
                    ConnectionPool::connectionCount);
            bindGauge(registry, MetricsConst.OKHTTP3_PREFIX + "idle.connection.count", connectionPool,
                    ConnectionPool::idleConnectionCount);
        }
    }

    private void bindGauge(MeterRegistry registry, String name, ConnectionPool connectionPool,
                           ToDoubleFunction<ConnectionPool> function) {
        Gauge.builder(name, connectionPool, function).tags(tags).register(registry);
    }
}
