package com.github.liaomengge.base_common.helper.async;

import com.github.liaomengge.base_common.helper.async.callback.BaseFutureCallback;
import com.github.liaomengge.base_common.utils.error.LyExceptionUtil;
import com.github.liaomengge.base_common.utils.thread.LyThreadPoolExecutorUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/6/10.
 */
@NoArgsConstructor
@AllArgsConstructor
public class Jdk8AsyncFuture implements InitializingBean {

    private ExecutorService executorService;

    public <P, V> CompletableFuture<V> asyncExec(P param, BaseFutureCallback<P, V> baseFutureCallback) {
        if (Objects.nonNull(executorService)) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return baseFutureCallback.execute(param);
                } catch (Exception e) {
                    throw LyExceptionUtil.unchecked(e);
                }
            }, executorService).handleAsync((v, throwable) -> {
                if (Objects.nonNull(throwable)) {
                    baseFutureCallback.doFailure(param, throwable);
                } else {
                    baseFutureCallback.doSuccess(param, v);
                }
                return v;
            }, executorService);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        if (Objects.isNull(executorService)) {
            executorService = LyThreadPoolExecutorUtil.buildCpuCoreThreadPool("async-jdk8-exec", 30L,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(32));
        }
    }
}
