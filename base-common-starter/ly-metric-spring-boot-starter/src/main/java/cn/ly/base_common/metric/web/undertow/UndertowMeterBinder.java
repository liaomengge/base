package cn.ly.base_common.metric.web.undertow;

import cn.ly.base_common.utils.log4j2.LyLogger;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.undertow.server.handlers.MetricsHandler;
import io.undertow.servlet.api.MetricsCollector;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.xnio.Version;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static cn.ly.base_common.metric.consts.MetricsConst.UNDERTOW_PREFIX;

/**
 * Created by liaomengge on 2020/9/16.
 * <p>
 * link: https://github.com/micrometer-metrics/micrometer/pull/1575/files
 */
public class UndertowMeterBinder implements MetricsCollector, ApplicationListener<ApplicationStartedEvent> {

    private static final Logger log = LyLogger.getInstance(UndertowMeterBinder.class);

    //可以查看NioXnio.register(XnioWorkerMXBean) - "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\""
    private static final String JMX_NAME = "org.xnio:type=Xnio,provider=\"nio\",worker=*";

    private final Iterable<Tag> tags;
    private final MeterRegistry registry;

    private MBeanServer mBeanServer;
    private Map<String, MetricsHandler> metricsHandlers;

    public UndertowMeterBinder(MeterRegistry registry) {
        this(Collections.emptyList(), registry);
    }

    public UndertowMeterBinder(Iterable<Tag> tags, MeterRegistry registry) {
        this.tags = tags;
        this.registry = registry;
        this.metricsHandlers = Maps.newHashMap();
    }

    @Override
    public void registerMetric(String servletName, MetricsHandler handler) {
        if (!"default".equalsIgnoreCase(servletName)) {
            this.metricsHandlers.put(servletName, handler);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            bind(registry);
        } catch (Exception e) {
            log.error("metric tomcat error", e);
        }
    }

    public void bind(MeterRegistry registry) {
        this.metricsHandlers.forEach((servletName, handler) -> registerGlobalRequestMetrics(registry, servletName,
                handler));
        registerThreadPoolMetrics(registry);
    }

    private void registerGlobalRequestMetrics(MeterRegistry registry, String servletName,
                                              MetricsHandler metricsHandler) {
        bindTimer(registry, servletName, UNDERTOW_PREFIX + "requests", metricsHandler,
                val -> val.getMetrics().getTotalRequests(), val2 -> val2.getMetrics().getMinRequestTime());
        bindTimeGauge(registry, servletName, UNDERTOW_PREFIX + "request.time.max", metricsHandler,
                val -> val.getMetrics().getMaxRequestTime());
        bindTimeGauge(registry, servletName, UNDERTOW_PREFIX + "request.time.min", metricsHandler,
                val -> val.getMetrics().getMinRequestTime());
        bindCounter(registry, servletName, UNDERTOW_PREFIX + "request.errors", metricsHandler,
                val -> val.getMetrics().getTotalErrors());
    }

    private void bindTimer(MeterRegistry registry, String servletName, String name, MetricsHandler metricsHandler,
                           ToLongFunction<MetricsHandler> countFunc, ToDoubleFunction<MetricsHandler> function) {
        FunctionTimer.builder(name, metricsHandler, countFunc, function, TimeUnit.MILLISECONDS)
                .tags(Tags.concat(tags, "servlet.name", servletName)).register(registry);
    }

    private void bindTimeGauge(MeterRegistry registry, String servletName, String name, MetricsHandler metricsHandler,
                               ToDoubleFunction<MetricsHandler> function) {
        TimeGauge.builder(name, metricsHandler, TimeUnit.MILLISECONDS, function)
                .tags(Tags.concat(tags, "servlet.name", servletName)).register(registry);
    }

    private void bindCounter(MeterRegistry registry, String servletName, String name, MetricsHandler metricsHandler,
                             ToDoubleFunction<MetricsHandler> function) {
        FunctionCounter.builder(name, metricsHandler, function)
                .tags(Tags.concat(tags, "servlet.name", servletName)).register(registry);
    }

    private void registerThreadPoolMetrics(MeterRegistry registry) {
        if (Objects.nonNull(this.mBeanServer)) {
            try {
                ObjectName objectName = new ObjectName(JMX_NAME);
                Set<ObjectName> objectNames = this.mBeanServer.queryNames(objectName, null);
                if (CollectionUtils.isNotEmpty(objectNames)) {
                    objectNames.stream().sorted(Comparator.reverseOrder()).findFirst().ifPresent(val -> {
                        bindGauge(registry, UNDERTOW_PREFIX + "io.thread.count", BaseUnits.THREADS,
                                mBeanServer, toDoubleFunction(mBeanServer, val, "IoThreadCount"));
                        bindGauge(registry, UNDERTOW_PREFIX + "worker.queue.size", BaseUnits.THREADS,
                                mBeanServer, toDoubleFunction(mBeanServer, val, "WorkerQueueSize"));
                        bindGauge(registry, UNDERTOW_PREFIX + "core.worker.pool.size", BaseUnits.THREADS,
                                mBeanServer, toDoubleFunction(mBeanServer, val, "CoreWorkerPoolSize"));
                        bindGauge(registry, UNDERTOW_PREFIX + "max.worker.pool.size", BaseUnits.THREADS,
                                mBeanServer, toDoubleFunction(mBeanServer, val, "MaxWorkerPoolSize"));
                        if (Version.VERSION.compareTo("3.5.0.Final") >= 0) {
                            bindGauge(registry, UNDERTOW_PREFIX + "busy.worker.thread.count", BaseUnits.THREADS,
                                    mBeanServer, toDoubleFunction(mBeanServer, val, "BusyWorkerThreadCount"));
                        }
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException("Error registering Undertow JMX based metrics");
            }
        }
    }

    private ToDoubleFunction<MBeanServer> toDoubleFunction(MBeanServer mBeanServer, ObjectName objectName,
                                                           String attribute) {
        return val -> safeDouble(() -> mBeanServer.getAttribute(objectName, attribute));
    }

    private double safeDouble(Callable<Object> callable) {
        try {
            return Double.parseDouble(callable.call().toString());
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private void bindGauge(MeterRegistry registry, String name, String unit, MBeanServer mBeanServer,
                           ToDoubleFunction<MBeanServer> function) {
        Gauge.builder(name, mBeanServer, function).tags(tags).baseUnit(unit).register(registry);
    }

    @PostConstruct
    public void init() {
        this.mBeanServer = this.getMBeanServer();
    }

    private MBeanServer getMBeanServer() {
        List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (!mBeanServers.isEmpty()) {
            return mBeanServers.get(0);
        }
        return ManagementFactory.getPlatformMBeanServer();
    }
}
