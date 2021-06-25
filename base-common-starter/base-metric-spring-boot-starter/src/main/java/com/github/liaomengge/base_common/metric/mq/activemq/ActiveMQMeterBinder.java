package com.github.liaomengge.base_common.metric.mq.activemq;

import com.github.liaomengge.base_common.mq.activemq.pool.MonitorPooledConnectionFactory;
import com.github.liaomengge.base_common.mq.activemq.pool.MonitorPooledConnectionFactory.PoolMonitor;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.pool.PooledConnectionFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

import static com.github.liaomengge.base_common.metric.consts.MetricsConst.ACTIVEMQ_PREFIX;

/**
 * Created by liaomengge on 2020/9/29.
 */
@Slf4j
public class ActiveMQMeterBinder implements MeterBinder {
    
    private final Iterable<Tag> tags;
    private final PooledConnectionFactory pooledConnectionFactory;

    public ActiveMQMeterBinder(PooledConnectionFactory pooledConnectionFactory) {
        this(Collections.emptyList(), pooledConnectionFactory);
    }

    public ActiveMQMeterBinder(Iterable<Tag> tags, PooledConnectionFactory pooledConnectionFactory) {
        this.tags = tags;
        this.pooledConnectionFactory = pooledConnectionFactory;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            registerMetrics(registry);
        } catch (Exception e) {
            log.error("metric activemq error", e);
        }
    }

    private void registerMetrics(MeterRegistry registry) {
        Optional.ofNullable(pooledConnectionFactory).ifPresent(val -> {
            if (val instanceof MonitorPooledConnectionFactory) {
                MonitorPooledConnectionFactory connectionFactory = (MonitorPooledConnectionFactory) val;
                PoolMonitor poolMonitor = connectionFactory.createPoolMonitor();
                bindGauge(registry, ACTIVEMQ_PREFIX + "max.total", poolMonitor,
                        PoolMonitor::getMaxTotal);
                bindGauge(registry, ACTIVEMQ_PREFIX + "num.active", poolMonitor,
                        PoolMonitor::getNumActive);
                bindGauge(registry, ACTIVEMQ_PREFIX + "num.idle", poolMonitor,
                        PoolMonitor::getNumIdle);
                bindGauge(registry, ACTIVEMQ_PREFIX + "num.waiters", poolMonitor,
                        PoolMonitor::getNumWaiters);
                bindTimeGauge(registry, ACTIVEMQ_PREFIX + "max.wait.millis", poolMonitor,
                        PoolMonitor::getMaxWaitMillis);
            }
        });
    }

    private void bindGauge(MeterRegistry registry, String name, PoolMonitor poolMonitor,
                           ToDoubleFunction<PoolMonitor> function) {
        Gauge.builder(name, poolMonitor, function).tags(tags).register(registry);
    }

    private void bindTimeGauge(MeterRegistry registry, String name, PoolMonitor poolMonitor,
                               ToDoubleFunction<PoolMonitor> function) {
        TimeGauge.builder(name, poolMonitor, TimeUnit.MILLISECONDS, function).tags(tags).register(registry);
    }
}
