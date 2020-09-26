package cn.ly.base_common.helper.mybatis.transaction.callback;

import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/12/20.
 */
@UtilityClass
public class TransactionCallbackManager {

    private static final ThreadLocal<Set<Runnable>> successThreadLocal =
            ThreadLocal.withInitial(() -> Sets.newHashSet());

    private static final ThreadLocal<Set<Consumer<Throwable>>> throwableThreadLocal =
            ThreadLocal.withInitial(() -> Sets.newHashSet());

    public void registerOnSuccess(Runnable runnable) {
        Set<Runnable> runnableSet = successThreadLocal.get();
        Optional.ofNullable(runnableSet).ifPresent(val -> val.add(runnable));
    }

    public void registerOnThrowable(Consumer<Throwable> consumer) {
        Set<Consumer<Throwable>> consumerSet = throwableThreadLocal.get();
        Optional.ofNullable(consumerSet).ifPresent(val -> val.add(consumer));
    }

    protected void invokeSuccess() {
        successThreadLocal.get().forEach(Runnable::run);
    }

    protected void invokeOnThrowable(Throwable t) {
        throwableThreadLocal.get().forEach(val -> val.accept(t));
    }

    protected void clear() {
        Set<Runnable> runnableSet = successThreadLocal.get();
        Optional.ofNullable(runnableSet).ifPresent(val -> val.clear());
        successThreadLocal.remove();

        Set<Consumer<Throwable>> consumerSet = throwableThreadLocal.get();
        Optional.ofNullable(consumerSet).ifPresent(val -> val.clear());
        throwableThreadLocal.remove();
    }
}
