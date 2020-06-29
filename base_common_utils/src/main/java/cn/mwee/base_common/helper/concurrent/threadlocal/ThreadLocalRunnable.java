package cn.mwee.base_common.helper.concurrent.threadlocal;

import java.util.Objects;

/**
 * Created by liaomengge on 2020/5/20.
 * 针对多种参数的设定, 可以直接通过接口函数new wrapper runnable
 */
public abstract class ThreadLocalRunnable<T> extends AbstractThreadLocal<T> implements Runnable {

    private Runnable delegate;
    private T context;

    public ThreadLocalRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    public ThreadLocalRunnable(Runnable delegate, T context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    public void run() {
        if (Objects.isNull(context)) {
            delegate.run();
            return;
        }
        set(context);
        try {
            delegate.run();
        } finally {
            clear();
        }
    }
}
