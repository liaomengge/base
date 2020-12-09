package com.github.liaomengge.base_common.helper.concurrent.threadlocal;

import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2020/5/20.
 * 针对多种参数的设定, 可以直接通过接口函数new wrapper callable
 */
public abstract class ThreadLocalCallable<T, V> extends AbstractThreadLocal<T> implements Callable<V> {

    private Callable<V> delegate;
    @Setter
    private T context;

    public ThreadLocalCallable(Callable<V> delegate) {
        this.delegate = delegate;
    }

    public ThreadLocalCallable(Callable<V> delegate, T context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public V call() throws Exception {
        if (Objects.isNull(context)) {
            return delegate.call();
        }
        set(context);
        try {
            return delegate.call();
        } finally {
            clear();
        }
    }
}
