package cn.ly.base_common.helper.concurrent.threadlocal;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by liaomengge on 2020/5/20.
 * 针对多种参数的设定, 可以直接通过接口函数new wrapper runnable
 */
public abstract class ThreadLocalConsumer<T, V> extends AbstractThreadLocal<T> implements Consumer<V> {

    private Consumer<V> delegate;
    private T context;

    public ThreadLocalConsumer(Consumer<V> delegate) {
        this.delegate = delegate;
    }

    public ThreadLocalConsumer(Consumer<V> delegate, T context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public void accept(V v) {
        if (Objects.isNull(context)) {
            delegate.accept(v);
            return;
        }
        set(context);
        try {
            delegate.accept(v);
        } finally {
            clear();
        }
    }
}
