package cn.ly.base_common.metric.web.undertow;

import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.number.LyMoreNumberUtil;
import cn.ly.base_common.utils.string.LyStringUtil;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.undertow.server.handlers.MetricsHandler;
import io.undertow.servlet.api.MetricsCollector;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static cn.ly.base_common.metric.consts.MetricsConst.UNDERTOW_PREFIX;

/**
 * Created by liaomengge on 2020/9/16.
 * <p>
 * link: https://github.com/micrometer-metrics/micrometer/pull/1575/files
 */
public class UndertowMetricsBinder implements MetricsCollector, ApplicationListener<ApplicationStartedEvent> {

    private static final Logger log = LyLogger.getInstance(UndertowMetricsBinder.class);

    //可以查看NioXnio.register(XnioWorkerMXBean) - "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\""
    private static final String JMX_NAME = "org.xnio:type=Xnio,provider=\"nio\",worker=*";
    private static final String JMX_NAME_BASE = "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\"";

    private final Iterable<Tag> tags;
    private final MeterRegistry meterRegistry;

    private MBeanServer mBeanServer;
    private Map<String, MetricsHandler> metricsHandlers;

    public UndertowMetricsBinder(MeterRegistry meterRegistry) {
        this(Collections.emptyList(), meterRegistry);
    }

    public UndertowMetricsBinder(Iterable<Tag> tags, MeterRegistry meterRegistry) {
        this.tags = tags;
        this.meterRegistry = meterRegistry;
        this.mBeanServer = this.getMBeanServer();
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
            bind(meterRegistry);
        } catch (Exception e) {
            log.error("metric tomcat error", e);
        }
    }

    private void bind(MeterRegistry registry) {
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
                    objectName =
                            objectNames.stream().sorted(Comparator.reverseOrder()).findFirst().orElse(new ObjectName(JMX_NAME_BASE));
                }
                double ioThreadCount = this.getDoubleAttribute(objectName, "IoThreadCount");
                double workerQueueSize = this.getDoubleAttribute(objectName, "WorkerQueueSize");
                double coreWorkerPoolSize = this.getDoubleAttribute(objectName, "CoreWorkerPoolSize");
                double maxWorkerPoolSize = this.getDoubleAttribute(objectName, "MaxWorkerPoolSize");
                bindGauge(registry, UNDERTOW_PREFIX + "io.thread.count", () -> ioThreadCount,
                        BaseUnits.THREADS);
                bindGauge(registry, UNDERTOW_PREFIX + "worker.queue.size", () -> workerQueueSize,
                        BaseUnits.TASKS);
                bindGauge(registry, UNDERTOW_PREFIX + "core.worker.pool.size", () -> coreWorkerPoolSize,
                        BaseUnits.THREADS);
                bindGauge(registry, UNDERTOW_PREFIX + "max.worker.pool.size", () -> maxWorkerPoolSize,
                        BaseUnits.THREADS);
                if (Version.VERSION.compareTo("3.5.0.Final") >= 0) {
                    double busyWorkerThreadCount = this.getDoubleAttribute(objectName, "BusyWorkerThreadCount");
                    bindGauge(registry, UNDERTOW_PREFIX + "busy.worker.thread.count", () -> busyWorkerThreadCount,
                            BaseUnits.THREADS);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error registering Undertow JMX based metrics");
            }
        }
    }

    private void bindGauge(MeterRegistry registry, String name, Supplier<Number> supplier, String unit) {
        Gauge.builder(name, supplier).baseUnit(unit).tags(tags).register(registry);
    }

    private double getDoubleAttribute(ObjectName objectName, String attribute) throws Exception {
        return LyMoreNumberUtil.toDouble(LyStringUtil.getValue(this.mBeanServer.getAttribute(objectName, attribute)));
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
