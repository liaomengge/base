package cn.ly.base_common.helper.async;

import cn.ly.base_common.helper.async.callback.BaseFutureCallback;
import cn.ly.base_common.helper.async.task.SingleTask;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.AllArgsConstructor;

/**
 * Created by liaomengge on 2019/6/10.
 */
@AllArgsConstructor
public class SpringAsyncFuture {

    private final ThreadPoolTaskExecutor lyThreadPoolTaskExecutor;

    public <P, V> ListenableFuture<V> asyncExec(P param, BaseFutureCallback<P, V> baseFutureCallback) {
        ListenableFuture<V> future = lyThreadPoolTaskExecutor.submitListenable(new SingleTask<>(param,
                baseFutureCallback));
        future.addCallback(new ListenableFutureCallback<V>() {
            @Override
            public void onFailure(Throwable ex) {
                baseFutureCallback.doFailure(param, ex);
            }

            @Override
            public void onSuccess(V result) {
                baseFutureCallback.doSuccess(param, result);
            }
        });
        return future;
    }
}
