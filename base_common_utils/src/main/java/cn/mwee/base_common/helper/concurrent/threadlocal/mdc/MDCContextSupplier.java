package cn.mwee.base_common.helper.concurrent.threadlocal.mdc;

import cn.mwee.base_common.helper.concurrent.threadlocal.ThreadLocalSupplier;
import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MDCContextSupplier<V> extends ThreadLocalSupplier<Map<String, String>, V> {

    public MDCContextSupplier(Supplier<V> delegate) {
        super(delegate);
    }

    public MDCContextSupplier(Supplier<V> delegate, Map<String, String> context) {
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

    public static <V> MDCContextSupplier<V> wrapSupplier(Supplier<V> supplier) {
        return new MDCContextSupplier(supplier, MDC.getCopyOfContextMap());
    }
}
