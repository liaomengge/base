package com.github.liaomengge.base_common.utils.thread;

import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolExecutor;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by liaomengge on 18/1/2.
 */
@Slf4j
@UtilityClass
public class LyThreadPoolExecutorUtil {

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue,
                                              RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                LyThreadFactoryBuilderUtil.build(threadName), handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit,
                                              int queueCapacity, RejectedExecutionHandler handler) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, keepAliveTime, unit,
                new LinkedBlockingQueue<>(queueCapacity), handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit, int queueCapacity) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, keepAliveTime, unit,
                new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              BlockingQueue<Runnable> workQueue,
                                              RejectedExecutionHandler handler) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, 0, TimeUnit.MILLISECONDS,
                workQueue, handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              int queueCapacity, RejectedExecutionHandler handler) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, new LinkedBlockingQueue<>(queueCapacity),
                handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              BlockingQueue<Runnable> workQueue) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, 0, TimeUnit.MILLISECONDS,
                workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              int queueCapacity) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        return buildThreadPool(1, 1, threadName, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    int queueCapacity, RejectedExecutionHandler handler) {
        return buildSingleThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity),
                handler);
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue) {
        return buildSingleThreadPool(threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    int queueCapacity) {
        return buildSingleThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        return buildSingleThreadPool(threadName, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, int queueCapacity,
                                                    RejectedExecutionHandler handler) {
        return buildSingleThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity), handler);
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, BlockingQueue<Runnable> workQueue) {
        return buildSingleThreadPool(threadName, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSingleThreadPool(String threadName, int queueCapacity) {
        return buildSingleThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        int cpuCore = LyRuntimeUtil.getCpuNum();
        return buildThreadPool(1, cpuCore, threadName, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    int queueCapacity, RejectedExecutionHandler handler) {
        return buildSimpleThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity),
                handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue) {
        return buildSimpleThreadPool(threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    int queueCapacity) {
        return buildSimpleThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        return buildSimpleThreadPool(threadName, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, int queueCapacity,
                                                    RejectedExecutionHandler handler) {
        return buildSimpleThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity), handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, BlockingQueue<Runnable> workQueue) {
        return buildSimpleThreadPool(threadName, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, int queueCapacity) {
        return buildSimpleThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     BlockingQueue<Runnable> workQueue,
                                                     RejectedExecutionHandler handler) {
        int cpuCore = LyRuntimeUtil.getCpuNum();
        return buildThreadPool(cpuCore, cpuCore * 2, threadName, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     int queueCapacity,
                                                     RejectedExecutionHandler handler) {
        return buildCpuCoreThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity),
                handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     BlockingQueue<Runnable> workQueue) {
        return buildCpuCoreThreadPool(threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     int queueCapacity) {
        return buildCpuCoreThreadPool(threadName, keepAliveTime, unit, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, BlockingQueue<Runnable> workQueue,
                                                     RejectedExecutionHandler handler) {
        return buildCpuCoreThreadPool(threadName, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, int queueCapacity,
                                                     RejectedExecutionHandler handler) {
        return buildCpuCoreThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity), handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, BlockingQueue<Runnable> workQueue) {
        return buildCpuCoreThreadPool(threadName, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, int queueCapacity) {
        return buildCpuCoreThreadPool(threadName, new LinkedBlockingQueue<>(queueCapacity));
    }

    public ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
        return Executors.newScheduledThreadPool(corePoolSize, threadFactory);
    }

    public ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String threadNameFormat) {
        return newScheduledThreadPool(corePoolSize, new ThreadFactoryBuilder().setNameFormat(threadNameFormat).build());
    }

    public void awaitShutdown(ThreadPoolExecutor executor) {
        awaitShutdown(executor, 10, 2);
    }

    /**
     * @param executor
     * @param awaitTerminationSeconds 等待超时时间
     * @param checkInterval           check时间间隔
     */
    public void awaitShutdown(ThreadPoolExecutor executor, int awaitTerminationSeconds, int checkInterval) {
        if (executor instanceof LyThreadPoolExecutor) {
            LyThreadPoolExecutor lyThreadPoolExecutor = (LyThreadPoolExecutor) executor;
            awaitShutdown(lyThreadPoolExecutor.getThreadName(), lyThreadPoolExecutor, awaitTerminationSeconds,
                    checkInterval);
            return;
        }
        awaitShutdown("default", executor, awaitTerminationSeconds, checkInterval);
    }

    /**
     * @param threadName              线程名
     * @param executor                线程池
     * @param awaitTerminationSeconds 等待超时时间
     * @param checkInterval           check时间间隔
     */
    public void awaitShutdown(String threadName, ThreadPoolExecutor executor,
                              int awaitTerminationSeconds, int checkInterval) {
        if (Objects.nonNull(executor)) {
            log.info("thread pool[{}] shutdown start...", executor);
            executor.shutdown();
            if (awaitTerminationSeconds > 0) {
                try {
                    for (long remaining = awaitTerminationSeconds; remaining > 0; remaining -= checkInterval) {
                        try {
                            if (executor.awaitTermination(Math.min(remaining, checkInterval),
                                    TimeUnit.SECONDS)) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            log.warn("Interrupted while waiting for executor [{}] to terminate", threadName);
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (Exception e) {
                    log.info("thread pool[{}] shutdown exception", executor, e);
                }
            }
            log.info("thread pool[{}] shutdown end...", executor);
        }
    }
}
