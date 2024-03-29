package com.github.liaomengge.base_common.metric.threadpool;

import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolWrappedExecutor;
import com.github.liaomengge.base_common.metric.consts.MetricsConst;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.ToDoubleFunction;

/**
 * Created by liaomengge on 2020/9/18.
 */
@Slf4j
public class ThreadPoolMetricsBinder implements MeterBinder, ApplicationContextAware {

    private final Iterable<Tag> tags;

    private ApplicationContext applicationContext;

    public ThreadPoolMetricsBinder() {
        this(Collections.emptyList());
    }

    public ThreadPoolMetricsBinder(Iterable<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ExecutorService monitor(MeterRegistry registry, String executorName,
                                          ExecutorService executorService) {
        return monitor(registry, Collections.emptyList(), executorName, executorService);
    }

    public static ExecutorService monitor(MeterRegistry registry, Iterable<Tag> tags, String executorName,
                                          ExecutorService executorService) {
        new ThreadPoolMetricsBinder(tags).bindToExecutorService(executorName, executorService, registry);
        return new TimedExecutorService(registry, executorService, executorName, "", tags);
    }

    public static void monitor(MeterRegistry registry, String executorName, AsyncTaskExecutor executor) {
        monitor(registry, Collections.emptyList(), executorName, executor);
    }

    public static void monitor(MeterRegistry registry, Iterable<Tag> tags, String executorName,
                               AsyncTaskExecutor executor) {
        new ThreadPoolMetricsBinder(tags).bindToSpringExecutor(executorName, executor, registry);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        try {
            Map<String, ThreadPoolTaskExecutor> taskExecutorMap =
                    applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
            Optional.ofNullable(taskExecutorMap)
                    .ifPresent(val -> val.forEach((key, value) -> bindToSpringExecutor(key, value, registry)));

            Map<String, LyThreadPoolTaskWrappedExecutor> taskWrappedExecutorMap =
                    applicationContext.getBeansOfType(LyThreadPoolTaskWrappedExecutor.class);
            Optional.ofNullable(taskWrappedExecutorMap)
                    .ifPresent(val -> val.forEach((key, value) -> bindToSpringExecutor(key, value, registry)));

            Map<String, ThreadPoolExecutor> executorMap =
                    applicationContext.getBeansOfType(ThreadPoolExecutor.class);
            Optional.ofNullable(executorMap)
                    .ifPresent(val -> val.forEach((key, value) -> bindToJdkExecutor(key, value, registry)));

            Map<String, LyThreadPoolWrappedExecutor> wrappedExecutorMap =
                    applicationContext.getBeansOfType(LyThreadPoolWrappedExecutor.class);
            Optional.ofNullable(wrappedExecutorMap)
                    .ifPresent(val -> val.forEach((key, value) -> bindToJdkExecutor(key, value, registry)));
        } catch (Exception e) {
            log.error("metric thread-pool error", e);
        }
    }

    private void bindToExecutorService(String executorName, Executor executor, MeterRegistry registry) {
        bindToSpringExecutor(executorName, executor, registry);
        bindToJdkExecutor(executorName, executor, registry);
    }

    private void bindToSpringExecutor(String executorName, Executor executor, MeterRegistry registry) {
        if (Objects.isNull(executor)) {
            return;
        }
        if (executor instanceof ThreadPoolTaskExecutor) {
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
            registerMetrics(registry, executorName, ThreadPoolUtil.unwrap(taskExecutor));
        } else if (executor instanceof LyThreadPoolTaskWrappedExecutor) {
            LyThreadPoolTaskWrappedExecutor taskWrappedExecutor = (LyThreadPoolTaskWrappedExecutor) executor;
            registerMetrics(registry, executorName, ThreadPoolUtil.unwrap(taskWrappedExecutor));
        }
    }

    private void bindToJdkExecutor(String executorName, Executor executor, MeterRegistry registry) {
        if (executor instanceof ThreadPoolExecutor) {
            registerMetrics(registry, executorName, (ThreadPoolExecutor) executor);
        } else if (executor instanceof LyThreadPoolWrappedExecutor) {
            LyThreadPoolWrappedExecutor wrappedExecutor = (LyThreadPoolWrappedExecutor) executor;
            registerMetrics(registry, executorName, ThreadPoolUtil.unwrap(wrappedExecutor));
        } else if (executor instanceof ForkJoinPool) {
            registerMetrics(registry, executorName, (ForkJoinPool) executor);
        }
    }

    private void registerMetrics(MeterRegistry registry, String executorName, ThreadPoolExecutor executor) {
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "pool.size", executor,
                ThreadPoolExecutor::getPoolSize);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "active.count", executor,
                ThreadPoolExecutor::getActiveCount);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "core.pool.size", executor,
                ThreadPoolExecutor::getCorePoolSize);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "maximum.pool.size", executor,
                ThreadPoolExecutor::getMaximumPoolSize);

        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "queued.size", executor,
                val -> val.getQueue().size());
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "queue.remaining.capacity", executor,
                val -> val.getQueue().remainingCapacity());

        buildCounter(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "completed.task.count", executor,
                ThreadPoolExecutor::getCompletedTaskCount);
    }

    private void registerMetrics(MeterRegistry registry, String executorName, ForkJoinPool forkJoinPool) {
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "queued.submission.count", forkJoinPool,
                ForkJoinPool::getStealCount);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "queued.task.count", forkJoinPool,
                ForkJoinPool::getQueuedTaskCount);
        buildCounter(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "steal.count", forkJoinPool,
                ForkJoinPool::getStealCount);

        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "pool.size", forkJoinPool,
                ForkJoinPool::getPoolSize);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "active.thread.count", forkJoinPool,
                ForkJoinPool::getActiveThreadCount);
        bindGauge(registry, executorName, MetricsConst.THREAD_POOL_PREFIX + "running.thread.count", forkJoinPool,
                ForkJoinPool::getRunningThreadCount);
    }

    private void buildCounter(MeterRegistry registry, String executorName, String name,
                              ThreadPoolExecutor executor, ToDoubleFunction<ThreadPoolExecutor> function) {
        FunctionCounter.builder(name, executor, function)
                .tags(Tags.concat(tags, MetricsConst.THREAD_POOL_PREFIX + "name", executorName)).register(registry);
    }

    private void bindGauge(MeterRegistry registry, String executorName, String name, ThreadPoolExecutor executor,
                           ToDoubleFunction<ThreadPoolExecutor> function) {
        Gauge.builder(name, executor, function)
                .tags(Tags.concat(tags, MetricsConst.THREAD_POOL_PREFIX + "name", executorName)).register(registry);
    }

    private void buildCounter(MeterRegistry registry, String executorName, String name,
                              ForkJoinPool forkJoinPool,
                              ToDoubleFunction<ForkJoinPool> function) {
        FunctionCounter.builder(name, forkJoinPool, function)
                .tags(Tags.concat(tags, "thread.pool.name", executorName)).register(registry);
    }

    private void bindGauge(MeterRegistry registry, String executorName, String name, ForkJoinPool forkJoinPool,
                           ToDoubleFunction<ForkJoinPool> function) {
        Gauge.builder(name, forkJoinPool, function)
                .tags(Tags.concat(tags, "thread.pool.name", executorName)).register(registry);
    }
}
