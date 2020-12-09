package com.github.liaomengge.base_common.helper.concurrent.threadlocal.map;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalCallable;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;

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
    public void set(Map<String, Object> mapContext) {
        ThreadLocalContextUtils.putAll(mapContext);
    }

    @Override
    public void clear() {
        ThreadLocalContextUtils.remove();
    }

    public static <V> MapContextCallable<V> wrapCallable(Callable<V> callable) {
        if (callable instanceof MapContextCallable) {
            return (MapContextCallable<V>) callable;
        }
        return new MapContextCallable(callable, ThreadLocalContextUtils.getAll());
    }
}
