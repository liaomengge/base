package cn.ly.base_common.helper.concurrent.threadlocal.map;

import cn.ly.base_common.helper.concurrent.threadlocal.ThreadLocalRunnable;
import cn.ly.base_common.support.threadlocal.ThreadLocalContextMap;

import java.util.Map;

import org.slf4j.MDC;

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
        ThreadLocalContextMap.putAll(contextMap);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    public static MapContextRunnable wrapRunnable(Runnable runnable) {
        return new MapContextRunnable(runnable, ThreadLocalContextMap.getAll());
    }
}
