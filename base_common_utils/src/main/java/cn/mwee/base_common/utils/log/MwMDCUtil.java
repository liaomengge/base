package cn.mwee.base_common.utils.log;

import org.slf4j.MDC;

/**
 * Created by liaomengge on 2018/9/5.
 */
public final class MwMDCUtil {

    public static final String MDC_WEB_REMOTE_IP = "MDC_WEB_REMOTE_IP";
    public static final String MDC_WEB_URI = "MDC_WEB_URI";

    public static final String MDC_WEB_ELAPSED_TIME = "MDC_WEB_ELAPSED_TIME";
    public static final String MDC_THIRD_ELAPSED_TIME = "MDC_THIRD_ELAPSED_TIME";

    private MwMDCUtil() {
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static void put(String val) {
        put(MDC_WEB_ELAPSED_TIME, val);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static void remove() {
        remove(MDC_WEB_ELAPSED_TIME);
    }

    public static void clear() {
        MDC.clear();
    }

}
