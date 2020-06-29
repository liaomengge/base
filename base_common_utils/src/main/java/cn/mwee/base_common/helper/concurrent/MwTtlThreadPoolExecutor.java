package cn.mwee.base_common.helper.concurrent;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.*;

/**
 * Created by liaomengge on 2019/10/9.
 */
public class MwTtlThreadPoolExecutor extends ThreadPoolExecutor {

    public MwTtlThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public MwTtlThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public MwTtlThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public MwTtlThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                   RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable runnable) {
        Runnable ttlRunnable = TtlRunnable.get(runnable);
        super.execute(ttlRunnable);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        Callable ttlCallable = TtlCallable.get(task);
        return super.submit(ttlCallable);
    }

    @Override
    public Future<?> submit(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submit(ttlRunnable);
    }
}
