package cn.ly.base_common.helper.concurrent.threadlocal.map;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalSupplier;
import cn.ly.base_common.support.threadlocal.ThreadLocalContextMap;

import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.MDC;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MapContextSupplier<V> extends ThreadLocalSupplier<Map<String, Object>, V> {

    public MapContextSupplier(Supplier<V> delegate) {
        super(delegate);
    }

    public MapContextSupplier(Supplier<V> delegate, Map<String, Object> context) {
        super(delegate, context);
    }

    @Override
    public void set(Map<String, Object> contextMap) {
        ThreadLocalContextMap.putAll(contextMap);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static <V> MapContextSupplier<V> wrapSupplier(Supplier<V> supplier) {
        return new MapContextSupplier(supplier, ThreadLocalContextMap.getAll());
    }
}
