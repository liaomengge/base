package cn.ly.base_common.helper.concurrent.threadlocal.map;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalConsumer;
import cn.ly.base_common.support.threadlocal.ThreadLocalContextMap;

import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.MDC;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MapContextConsumer<V> extends ThreadLocalConsumer<Map<String, Object>, V> {

    public MapContextConsumer(Consumer<V> delegate) {
        super(delegate);
    }

    public MapContextConsumer(Consumer<V> delegate, Map<String, Object> context) {
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

    public static <V> MapContextConsumer<V> wrapConsumer(Consumer<V> consumer) {
        return new MapContextConsumer(consumer, ThreadLocalContextMap.getAll());
    }
}
