package com.github.liaomengge.base_common.helper.concurrent.threadlocal.map;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalSupplier;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;

import java.util.Map;
import java.util.function.Supplier;

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
    public void set(Map<String, Object> mapContext) {
        ThreadLocalContextUtils.putAll(mapContext);
    }

    @Override
    public void clear() {
        ThreadLocalContextUtils.remove();
    }

    public static <V> MapContextSupplier<V> wrapSupplier(Supplier<V> supplier) {
        if (supplier instanceof MapContextSupplier) {
            return (MapContextSupplier<V>) supplier;
        }
        return new MapContextSupplier(supplier, ThreadLocalContextUtils.getAll());
    }
}
