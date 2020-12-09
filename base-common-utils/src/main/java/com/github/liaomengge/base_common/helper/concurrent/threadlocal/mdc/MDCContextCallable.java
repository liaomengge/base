package com.github.liaomengge.base_common.helper.concurrent.threadlocal.mdc;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MDCContextCallable<V> extends ThreadLocalCallable<Map<String, String>, V> {

    public MDCContextCallable(Callable<V> delegate) {
        super(delegate);
    }

    public MDCContextCallable(Callable<V> delegate, Map<String, String> context) {
        super(delegate, context);
    }

    @Override
    public void set(Map<String, String> mdcContext) {
        MDC.setContextMap(mdcContext);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static <V> MDCContextCallable<V> wrapCallable(Callable<V> callable) {
        if (callable instanceof MDCContextCallable) {
            return (MDCContextCallable<V>) callable;
        }
        return new MDCContextCallable(callable, MDC.getCopyOfContextMap());
    }
}
