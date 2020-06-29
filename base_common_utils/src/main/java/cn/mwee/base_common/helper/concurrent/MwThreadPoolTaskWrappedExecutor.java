package cn.mwee.base_common.helper.concurrent;

import cn.mwee.base_common.utils.error.MwThrowableUtil;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by liaomengge on 2019/1/7.
 */
@AllArgsConstructor
public class MwThreadPoolTaskWrappedExecutor implements AsyncListenableTaskExecutor, MwThreadHelper, DisposableBean {

    private static final Logger logger = MwLogger.getInstance(MwThreadPoolTaskWrappedExecutor.class);

    @Getter
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void execute(Runnable task, long startTimeout) {
        threadPoolTaskExecutor.execute(createWrappedRunnable(task), startTimeout);
    }

    @Override
    public void execute(Runnable task) {
        threadPoolTaskExecutor.execute(createWrappedRunnable(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return threadPoolTaskExecutor.submit(createWrappedRunnable(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolTaskExecutor.submit(createWrappedCallable(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return threadPoolTaskExecutor.submitListenable(createWrappedRunnable(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return threadPoolTaskExecutor.submitListenable(createWrappedCallable(task));
    }

    @Override
    public void doExceptionHandle(Throwable e) {
        logger.error("Current Thread[{}], Exec Exception ===> {}", Thread.currentThread().getName(),
                MwThrowableUtil.getStackTrace(e));
    }

    @Override
    public void destroy() {
        threadPoolTaskExecutor.destroy();
    }
}
