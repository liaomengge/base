package com.github.liaomengge.base_common.helper.concurrent.threadlocal.map;

import com.github.liaomengge.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils;
import org.slf4j.MDC;

import java.util.Map;

import static com.github.liaomengge.base_common.support.threadlocal.ThreadLocalContextUtils.getBaseThreadLocalContextMap;

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
    public void set(Map<String, Object> contextMap) {
        ThreadLocalContextUtils.putAll(getBaseThreadLocalContextMap(), contextMap);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static MapContextRunnable wrapRunnable(Runnable runnable) {
        return new MapContextRunnable(runnable, ThreadLocalContextUtils.getAll(getBaseThreadLocalContextMap()));
    }
}
