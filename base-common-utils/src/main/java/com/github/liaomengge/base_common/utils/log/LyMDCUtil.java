package com.github.liaomengge.base_common.utils.log;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

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

    public void put(String key, String value) {
        MDC.put(key, value);
    }

    public void put(String val) {
        put(MDC_API_ELAPSED_MILLI_TIME, val);
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
