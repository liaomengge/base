package cn.ly.base_common.utils.log;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

/**
 * Created by liaomengge on 2018/9/5.
 */
@UtilityClass
public class MwMDCUtil {

    public final String MDC_WEB_REMOTE_IP = "MDC_WEB_REMOTE_IP";
    public final String MDC_WEB_URI = "MDC_WEB_URI";

    public final String MDC_WEB_ELAPSED_TIME = "MDC_WEB_ELAPSED_TIME";
    public final String MDC_THIRD_ELAPSED_TIME = "MDC_THIRD_ELAPSED_TIME";

    public String get(String key) {
        return MDC.get(key);
    }

    public void put(String key, String value) {
        MDC.put(key, value);
    }

    public void put(String val) {
        put(MDC_WEB_ELAPSED_TIME, val);
    }

    public void remove(String key) {
        MDC.remove(key);
    }

    public void remove() {
        remove(MDC_WEB_ELAPSED_TIME);
    }

    public void clear() {
        MDC.clear();
    }

}
