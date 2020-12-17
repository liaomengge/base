package com.github.liaomengge.base_common.helper.concurrent.threadlocal.map;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;

import java.util.Map;

/**
 * Created by liaomengge on 2020/5/20.
 */
public class MapContextRunnable extends ThreadLocalRunnable<Map<String, Object>> {

    public MapContextRunnable(Runnable delegate) {
        super(delegate);
    }

    public MapContextRunnable(Runnable delegate, Map<String, Object> context) {
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

    public static MapContextRunnable wrapRunnable(Runnable runnable) {
        if (runnable instanceof MapContextRunnable) {
            return (MapContextRunnable) runnable;
        }
        return new MapContextRunnable(runnable, ThreadLocalContextUtils.getAll());
    }
}
