package com.github.liaomengge.base_common.utils.callback;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/12/20.
 */
@UtilityClass
public class LyMethodCallbackUtil {

    public <P, V> V doCallback(P param, BaseExecuteCallback<P, V> executeCallback) {
        V result = null;
        try {
            result = executeCallback.execute(param);
            executeCallback.doSuccess(param, result);
        } catch (Throwable t) {
            executeCallback.doFailure(param, t);
        }
        return result;
    }

    public <P, V> V doCallback(P param, Function<P, V> function, BaseCallback<P, V> callback) {
        V result = null;
        try {
            result = function.apply(param);
            callback.doSuccess(param, result);
        } catch (Throwable t) {
            callback.doFailure(param, t);
        }
        return result;
    }

    public <P, V> V doCallback(P param, Function<P, V> function, BiConsumer<P, V> successConsumer,
                               BiConsumer<P, Throwable> failConsumer) {
        V result = null;
        try {
            result = function.apply(param);
            successConsumer.accept(param, result);
        } catch (Throwable t) {
            failConsumer.accept(param, t);
        }
        return result;
    }

    public <V> V doCallback(Supplier<V> supplier, Consumer<V> successConsumer, Consumer<Throwable> failConsumer) {
        V result = null;
        try {
            result = supplier.get();
            successConsumer.accept(result);
        } catch (Throwable t) {
            failConsumer.accept(t);
        }
        return result;
    }

    public <V> void doCallback(Runnable runnable, Runnable successConsumer, Consumer<Throwable> failConsumer) {
        try {
            runnable.run();
            successConsumer.run();
        } catch (Throwable t) {
            failConsumer.accept(t);
        }
    }

    public interface BaseCallback<P, V> {

        default void doSuccess(P param, V result) {
        }

        default void doFailure(P param, Throwable throwable) {
        }
    }

    public interface BaseExecuteCallback<P, V> extends BaseCallback<P, V> {

        V execute(P param) throws Exception;
    }
}
