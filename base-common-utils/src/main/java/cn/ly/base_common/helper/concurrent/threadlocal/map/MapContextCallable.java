package cn.ly.base_common.helper.concurrent.threadlocal.map;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;
import cn.ly.base_common.support.threadlocal.ThreadLocalContextMap;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MapContextCallable<V> extends ThreadLocalCallable<Map<String, Object>, V> {

    public MapContextCallable(Callable<V> delegate) {
        super(delegate);
    }

    public MapContextCallable(Callable<V> delegate, Map<String, Object> context) {
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

    public static <V> MapContextCallable<V> wrapCallable(Callable<V> callable) {
        return new MapContextCallable(callable, ThreadLocalContextMap.getAll());
    }
}
