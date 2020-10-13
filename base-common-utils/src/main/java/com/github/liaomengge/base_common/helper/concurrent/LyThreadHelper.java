package com.github.liaomengge.base_common.helper.concurrent;

import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2019/1/7.
 */
public interface LyThreadHelper {

    default Runnable createWrappedRunnable(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Throwable e) {
                doExceptionHandle(e);
            }
        };
    }

    default <T> Callable<T> createWrappedCallable(Callable<T> task) {
        return () -> {
            try {
                return task.call();
            } catch (Throwable e) {
                doExceptionHandle(e);
                throw e;
            }
        };
    }

    default void doExceptionHandle(Throwable e) {
    }
}
