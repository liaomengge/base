package com.github.liaomengge.base_common.helper.concurrent.threadlocal.mdc;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalConsumer;
import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Consumer;

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
    public void set(Map<String, String> mdcContext) {
        MDC.setContextMap(mdcContext);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static <V> MDCContextConsumer<V> wrapConsumer(Consumer<V> consumer) {
        if (consumer instanceof MDCContextConsumer) {
            return (MDCContextConsumer<V>) consumer;
        }
        return new MDCContextConsumer(consumer, MDC.getCopyOfContextMap());
    }
}
