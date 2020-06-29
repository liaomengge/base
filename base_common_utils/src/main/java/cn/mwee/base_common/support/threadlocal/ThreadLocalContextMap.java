package cn.mwee.base_common.support.threadlocal;

import lombok.experimental.UtilityClass;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/5/29.
 */
@UtilityClass
public class ThreadLocalContextMap {

    private static ThreadLocal<Map<String, Object>> threadLocal = new NamedThreadLocal("MW-THREAD-LOCAL-CONTEXT-MAP");

    public void put(String key, Object val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, Object> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocal.set(map);
        }
        map.put(key, val);
    }

    public void putAll(Map<String, Object> map) {
        Map<String, Object> localMap = threadLocal.get();
        if (Objects.isNull(localMap)) {
            threadLocal.set(map);
            return;
        }
        localMap.putAll(map);
    }

    public Object get(String key) {
        Map<String, Object> map = threadLocal.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        }
        return null;
    }

    public Map<String, Object> getAll() {
        return threadLocal.get();
    }

    public void clear() {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            map.clear();
        }
        threadLocal.remove();
    }
}
