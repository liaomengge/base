package cn.ly.base_common.metric.web.undertow;

import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.number.LyMoreNumberUtil;
import cn.ly.base_common.utils.string.LyStringUtil;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.undertow.server.handlers.MetricsHandler;
import org.slf4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.xnio.Version;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static cn.ly.base_common.metric.consts.MetricsConst.UNDERTOW_PREFIX;

/**
 * Created by liaomengge on 2020/9/16.
 */
public class UndertowMeterBinder implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LyLogger.getInstance(UndertowMeterBinder.class);

    //可以查看NioXnio.register(XnioWorkerMXBean)
    private static final String JMX_NAME_BASE = "org.xnio:type=Xnio,provider=\"nio\",worker=\"XNIO-1\"";

    private final Iterable<Tag> tags;
    private final MeterRegistry meterRegistry;
    private final UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper;

    private MBeanServer mBeanServer;

    public UndertowMeterBinder(MeterRegistry meterRegistry,
                               UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        this(Collections.emptyList(), meterRegistry, undertowMetricsHandlerWrapper);
    }

    public UndertowMeterBinder(Iterable<Tag> tags, MeterRegistry meterRegistry,
                               UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        this.tags = tags;
        this.meterRegistry = meterRegistry;
        this.undertowMetricsHandlerWrapper = undertowMetricsHandlerWrapper;
        this.mBeanServer = this.getMBeanServer();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            bind(meterRegistry, undertowMetricsHandlerWrapper.getMetricsHandler());
        } catch (Exception e) {
            log.error("metric tomcat error", e);
        }
    }

    private void bind(MeterRegistry registry, MetricsHandler metricsHandler) {
        registerGlobalRequestMetrics(registry, metricsHandler);
        registerThreadPoolMetrics(registry);
    }

    private void registerGlobalRequestMetrics(MeterRegistry registry, MetricsHandler metricsHandler) {
        bindTimer(registry, UNDERTOW_PREFIX + "requests", metricsHandler,
                val -> val.getMetrics().getTotalRequests(), val2 -> val2.getMetrics().getMinRequestTime());
        bindTimeGauge(registry, UNDERTOW_PREFIX + "request.time.max", metricsHandler,
                val -> val.getMetrics().getMaxRequestTime());
        bindTimeGauge(registry, UNDERTOW_PREFIX + "request.time.min", metricsHandler,
                val -> val.getMetrics().getMinRequestTime());
        bindCounter(registry, UNDERTOW_PREFIX + "request.errors", metricsHandler,
                val -> val.getMetrics().getTotalErrors());
    }

    private void bindTimer(MeterRegistry registry, String name, MetricsHandler metricsHandler,
                           ToLongFunction<MetricsHandler> countFunc, ToDoubleFunction<MetricsHandler> function) {
        FunctionTimer.builder(name, metricsHandler, countFunc, function, TimeUnit.MILLISECONDS).tags(tags).register(registry);
    }

    private void bindTimeGauge(MeterRegistry registry, String name, MetricsHandler metricsHandler,
                               ToDoubleFunction<MetricsHandler> function) {
        TimeGauge.builder(name, metricsHandler, TimeUnit.MILLISECONDS, function).tags(tags).register(registry);
    }

    private void bindCounter(MeterRegistry registry, String name, MetricsHandler metricsHandler,
                             ToDoubleFunction<MetricsHandler> function) {
        FunctionCounter.builder(name, metricsHandler, function).tags(tags).register(registry);
    }

    private void registerThreadPoolMetrics(MeterRegistry registry) {
        if (Objects.nonNull(this.mBeanServer)) {
            try {
                ObjectName objectName = new ObjectName(JMX_NAME_BASE);

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
