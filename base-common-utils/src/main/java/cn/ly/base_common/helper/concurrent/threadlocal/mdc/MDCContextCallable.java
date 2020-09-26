package cn.ly.base_common.helper.concurrent.threadlocal.mdc;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.MDC;

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
    public void set(Map<String, String> contextMap) {
        MDC.setContextMap(contextMap);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static <V> MDCContextCallable<V> wrapCallable(Callable<V> callable) {
        return new MDCContextCallable(callable, MDC.getCopyOfContextMap());
    }
}
