package cn.ly.base_common.helper.async;

import cn.ly.base_common.helper.async.callback.BaseFutureCallback;
import cn.ly.base_common.helper.async.task.SingleTask;
import cn.ly.base_common.utils.thread.LyThreadPoolExecutorUtil;

import com.google.common.util.concurrent.*;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.InitializingBean;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Created by liaomengge on 17/7/21.
 */
@NoArgsConstructor
@AllArgsConstructor
public class GuavaAsyncFuture implements InitializingBean {

    private ListeningExecutorService executorService;

    public <P, V> ListenableFuture<V> asyncExec(P param, BaseFutureCallback<P, V> baseFutureCallback) {
        if (Objects.nonNull(executorService)) {
            ListenableFuture<V> future = executorService.submit(new SingleTask<>(param, baseFutureCallback));
            Futures.addCallback(future, new FutureCallback<V>() {
                @Override
                public void onSuccess(@Nullable V result) {
                    baseFutureCallback.doSuccess(param, result);
                }

                @Override
                public void onFailure(Throwable t) {
                    baseFutureCallback.doFailure(param, t);
                }
            }, executorService);

            return future;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        if (Objects.isNull(executorService)) {
            ExecutorService asyncExecutorService = LyThreadPoolExecutorUtil.buildCpuCoreThreadPool("async-exec", 30L,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(32));
            executorService = MoreExecutors.listeningDecorator(asyncExecutorService);
        }
    }
}
