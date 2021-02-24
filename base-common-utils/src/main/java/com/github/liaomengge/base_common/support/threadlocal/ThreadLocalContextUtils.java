package com.github.liaomengge.base_common.support.threadlocal;

import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/11/2.
 */
@UtilityClass
public class ThreadLocalContextUtils {

    private ThreadLocal<Map<String, Object>> BASE_THREAD_LOCAL_CONTEXT_MAP =
            LyThreadLocalUtil.getNamedThreadLocal("base-thread-local-context", HashMap::new);

    public void put(String key, Object value) {
        put(getBaseThreadLocalContextMap(), key, value);
    }

    public void put(ThreadLocal<Map<String, Object>> threadLocalMap, String key, Object value) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, Object> map = threadLocalMap.get();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
            threadLocalMap.set(map);
        }
        map.put(key, value);
    }

    public void putAll(Map<String, Object> map) {
        putAll(getBaseThreadLocalContextMap(), map);
    }

    public void putAll(ThreadLocal<Map<String, Object>> threadLocalMap, Map<String, Object> map) {
        Map<String, Object> localMap = threadLocalMap.get();
        if (Objects.isNull(localMap)) {
            threadLocalMap.set(map);
            return;
        }
        localMap.putAll(map);
    }

    public <T> T get(String key) {
        return get(getBaseThreadLocalContextMap(), key);
    }

    public <T> T get(ThreadLocal<Map<String, Object>> threadLocalMap, String key) {
        Map<String, Object> map = threadLocalMap.get();
        if (Objects.nonNull(map) && Objects.nonNull(key)) {
            return (T) map.get(key);
        }
        return null;
    }

    public Map<String, Object> getAll() {
        return getAll(getBaseThreadLocalContextMap());
    }

    public Map<String, Object> getAll(ThreadLocal<Map<String, Object>> threadLocalMap) {
        return threadLocalMap.get();
    }

    public void remove() {
        remove(getBaseThreadLocalContextMap());
    }

    public void remove(ThreadLocal<Map<String, Object>> threadLocalMap) {
        threadLocalMap.remove();
    }

    public ThreadLocal<Map<String, Object>> getBaseThreadLocalContextMap() {
        return BASE_THREAD_LOCAL_CONTEXT_MAP;
    }
}
