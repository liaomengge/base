package com.github.liaomengge.base_common.utils.log;

import com.google.common.io.Closer;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by liaomengge on 2018/9/5.
 */
@UtilityClass
public class LyMDCUtil {

    public final String MDC_API_URI = "MDC_API_URI";
    public final String MDC_API_REMOTE_IP = "MDC_API_REMOTE_IP";
    public final String MDC_API_ELAPSED_MILLI_TIME = "MDC_API_ELAPSED_MILLI_TIME";
    public final String MDC_CLIENT_ELAPSED_MILLI_TIME = "MDC_CLIENT_ELAPSED_MILLI_TIME";

    public String get(String key) {
        return MDC.get(key);
    }

    public void put(String value) {
        put(MDC_API_ELAPSED_MILLI_TIME, value);
    }

    public void put(String key, String value) {
        MDC.put(key, value);
    }

    public void put(Map<String, String> map) {
        if (MapUtils.isNotEmpty(map)) {
            map.entrySet().stream().forEach(entry -> put(entry.getKey(), entry.getValue()));
        }
    }

    public MDC.MDCCloseable putCloseable(String key, String value) {
        return MDC.putCloseable(key, value);
    }

    public void register(Closer closer, String key, String value) {
        closer.register(putCloseable(key, value));
    }

    public void register(Closer closer, Map<String, String> map) {
        if (MapUtils.isNotEmpty(map)) {
            map.entrySet().stream().forEach(entry -> closer.register(putCloseable(entry.getKey(), entry.getValue())));
        }
    }

    public void remove(String key) {
        MDC.remove(key);
    }

    public void remove() {
        remove(MDC_API_ELAPSED_MILLI_TIME);
    }

    public void clear() {
        MDC.clear();
    }

}
