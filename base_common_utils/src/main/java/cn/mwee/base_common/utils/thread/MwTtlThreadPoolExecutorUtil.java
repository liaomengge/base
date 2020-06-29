package cn.mwee.base_common.utils.thread;

import cn.mwee.base_common.helper.concurrent.MwTtlThreadPoolExecutor;
import lombok.experimental.UtilityClass;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 18/1/2.
 */
@UtilityClass
public class MwTtlThreadPoolExecutorUtil {

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue,
                                              RejectedExecutionHandler handler) {
        return new MwTtlThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                MwThreadFactoryBuilderUtil.build(threadName), handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              long keepAliveTime, TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              BlockingQueue<Runnable> workQueue,
                                              RejectedExecutionHandler handler) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, 0, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildThreadPool(int corePoolSize, int maximumPoolSize, String threadName,
                                              BlockingQueue<Runnable> workQueue) {
        return buildThreadPool(corePoolSize, maximumPoolSize, threadName, 0, TimeUnit.MILLISECONDS, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        int cpuCore = MwRuntimeUtil.getCpuNum();
        return buildThreadPool(1, cpuCore, threadName, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue) {
        return buildSimpleThreadPool(threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, BlockingQueue<Runnable> workQueue,
                                                    RejectedExecutionHandler handler) {
        return buildSimpleThreadPool(threadName, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildSimpleThreadPool(String threadName, BlockingQueue<Runnable> workQueue) {
        return buildSimpleThreadPool(threadName, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     BlockingQueue<Runnable> workQueue,
                                                     RejectedExecutionHandler handler) {
        int cpuCore = MwRuntimeUtil.getCpuNum();
        return buildThreadPool(cpuCore, cpuCore * 2, threadName, keepAliveTime, unit, workQueue, handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, long keepAliveTime, TimeUnit unit,
                                                     BlockingQueue<Runnable> workQueue) {
        return buildCpuCoreThreadPool(threadName, keepAliveTime, unit, workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, BlockingQueue<Runnable> workQueue,
                                                     RejectedExecutionHandler handler) {
        return buildCpuCoreThreadPool(threadName, 0L, TimeUnit.MILLISECONDS, workQueue, handler);
    }

    public ThreadPoolExecutor buildCpuCoreThreadPool(String threadName, BlockingQueue<Runnable> workQueue) {
        return buildCpuCoreThreadPool(threadName, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
