package cn.ly.base_common.utils.thread;

import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.shutdown.LyShutdownUtil;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by liaomengge on 18/1/2.
 */
@UtilityClass
public class LyThreadPoolExecutorUtil {

    private final Logger log = LyLogger.getInstance(LyThreadPoolExecutorUtil.class);

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

    public void registerShutdownHook(ThreadPoolExecutor threadPoolExecutor) {
        registerShutdownHook(threadPoolExecutor, 10, 2);
    }

    /**
     * @param threadPoolExecutor
     * @param awaitTerminationSeconds 等待超时时间
     * @param checkInterval           check时间间隔
     */
    public void registerShutdownHook(ThreadPoolExecutor threadPoolExecutor, int awaitTerminationSeconds,
                                     int checkInterval) {
        LyShutdownUtil.registerShutdownHook(() -> {
            if (Objects.nonNull(threadPoolExecutor)) {
                log.info("thread pool[{}] shutdown start...", threadPoolExecutor);
                threadPoolExecutor.shutdown();
                try {
                    for (long remaining = awaitTerminationSeconds; remaining > 0; remaining -= checkInterval) {
                        try {
                            if (threadPoolExecutor.awaitTermination(Math.min(remaining, checkInterval),
                                    TimeUnit.SECONDS)) {
                                continue;
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (Exception e) {
                    log.info("thread pool[" + threadPoolExecutor + "] shutdown exception", e);
                }
                log.info("thread pool[{}] shutdown end...", threadPoolExecutor);
            }
        });
    }
}
