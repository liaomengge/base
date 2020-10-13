package com.github.liaomengge.base_common.helper.concurrent.threadlocal;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/5/20.
 * 针对多种参数的设定, 可以直接通过接口函数new wrapper runnable
 */
public abstract class ThreadLocalSupplier<T, V> extends AbstractThreadLocal<T> implements Supplier<V> {

    private Supplier<V> delegate;
    private T context;

    public ThreadLocalSupplier(Supplier<V> delegate) {
        this.delegate = delegate;
    }

    public ThreadLocalSupplier(Supplier<V> delegate, T context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public V get() {
        if (Objects.isNull(context)) {
            return delegate.get();
        }
        set(context);
        try {
            return delegate.get();
        } finally {
            clear();
        }
    }
}
