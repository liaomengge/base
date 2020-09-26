package cn.ly.base_common.helper.concurrent.threadlocal.mdc;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalConsumer;

import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.MDC;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MDCContextConsumer<V> extends ThreadLocalConsumer<Map<String, String>, V> {

    public MDCContextConsumer(Consumer<V> delegate) {
        super(delegate);
    }

    public MDCContextConsumer(Consumer<V> delegate, Map<String, String> context) {
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

    public static <V> MDCContextConsumer<V> wrapConsumer(Consumer<V> consumer) {
        return new MDCContextConsumer(consumer, MDC.getCopyOfContextMap());
    }
}
