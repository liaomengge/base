package com.github.liaomengge.base_common.support.threadlocal;

import lombok.experimental.UtilityClass;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/11/2.
 */
@UtilityClass
public class ThreadLocalContextUtils {

    private static ThreadLocal<Map<String, Object>> baseThreadLocalContextMap =
            new NamedThreadLocal("BASE-THREAD-LOCAL-CONTEXT-MAP");

    public void put(ThreadLocal<Map<String, Object>> threadLocalMap, String key, Object value) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, Object> map = threadLocalMap.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocalMap.set(map);
        }
        map.put(key, value);
    }

    public void putAll(ThreadLocal<Map<String, Object>> threadLocalMap, Map<String, Object> map) {
        Map<String, Object> localMap = threadLocalMap.get();
        if (Objects.isNull(localMap)) {
            threadLocalMap.set(map);
            return;
        }
        localMap.putAll(map);
    }

    public <T> T get(ThreadLocal<Map<String, Object>> threadLocalMap, String key) {
        Map<String, Object> map = threadLocalMap.get();
        if ((map != null) && (key != null)) {
            return (T) map.get(key);
        }
        return null;
    }

    public Map<String, Object> getAll(ThreadLocal<Map<String, Object>> threadLocalMap) {
        return threadLocalMap.get();
    }

    public void remove(ThreadLocal<Map<String, Object>> threadLocalMap) {
        threadLocalMap.remove();
    }

    public static ThreadLocal<Map<String, Object>> getBaseThreadLocalContextMap() {
        return baseThreadLocalContextMap;
    }
}
