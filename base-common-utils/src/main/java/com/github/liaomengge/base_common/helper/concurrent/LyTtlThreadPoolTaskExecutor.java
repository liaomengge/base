package com.github.liaomengge.base_common.helper.concurrent;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by liaomengge on 2019/10/9.
 */
public class LyTtlThreadPoolTaskExecutor extends LyThreadPoolTaskExecutor {

    private static final long serialVersionUID = -1045078888076452175L;

    public LyTtlThreadPoolTaskExecutor(String threadName) {
        super(threadName);
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

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submitListenable(ttlRunnable);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        Callable ttlCallable = TtlCallable.get(task);
        return super.submitListenable(ttlCallable);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        super.execute(ttlRunnable, startTimeout);
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        super.cancelRemainingTask(ttlRunnable);
    }
}
