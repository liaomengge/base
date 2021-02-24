package com.github.liaomengge.base_common.helper.mybatis.transaction.callback;

import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by liaomengge on 2019/12/20.
 */
@UtilityClass
public class TransactionCallbackManager {

    private static ThreadLocal<Set<Runnable>> SUCCESS_THREAD_LOCAL =
            LyThreadLocalUtil.getNamedThreadLocal("success", Sets::newHashSet);

    private static ThreadLocal<Set<Consumer<Throwable>>> THROWABLE_THREAD_LOCAL =
            LyThreadLocalUtil.getNamedThreadLocal("throwable", Sets::newHashSet);
    

    public void registerOnSuccess(Runnable runnable) {
        Set<Runnable> runnableSet = SUCCESS_THREAD_LOCAL.get();
        Optional.ofNullable(runnableSet).ifPresent(val -> val.add(runnable));
    }

    public void registerOnThrowable(Consumer<Throwable> consumer) {
        Set<Consumer<Throwable>> consumerSet = THROWABLE_THREAD_LOCAL.get();
        Optional.ofNullable(consumerSet).ifPresent(val -> val.add(consumer));
    }

    protected void invokeSuccess() {
        SUCCESS_THREAD_LOCAL.get().forEach(Runnable::run);
    }

    protected void invokeOnThrowable(Throwable t) {
        THROWABLE_THREAD_LOCAL.get().forEach(val -> val.accept(t));
    }

    protected void clear() {
        Set<Runnable> runnableSet = SUCCESS_THREAD_LOCAL.get();
        Optional.ofNullable(runnableSet).ifPresent(val -> val.clear());
        SUCCESS_THREAD_LOCAL.remove();

        Set<Consumer<Throwable>> consumerSet = THROWABLE_THREAD_LOCAL.get();
        Optional.ofNullable(consumerSet).ifPresent(val -> val.clear());
        THROWABLE_THREAD_LOCAL.remove();
    }
}
