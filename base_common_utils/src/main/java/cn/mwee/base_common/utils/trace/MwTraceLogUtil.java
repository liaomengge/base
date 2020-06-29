package cn.mwee.base_common.utils.trace;

import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.misc.MwIdGeneratorUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaomengge on 17/5/25.
 */
public final class MwTraceLogUtil {

    public static final String TRACE_ID = "x-mw-trace-id";

    private MwTraceLogUtil() {
    }

    private static final TransmittableThreadLocal<Map<String, String>> TRANSMITTABLE_THREAD_LOCAL =
            new TransmittableThreadLocal<>();

    public static void put(String val) {
        put(TRACE_ID, val);
    }

    public static void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if (map == null) {
            map = new HashMap<>();
            TRANSMITTABLE_THREAD_LOCAL.set(map);
        }
        map.put(key, val);
    }

    public static String get() {
        return get(TRACE_ID);
    }

    public static String get(String key) {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        }
        return null;
    }

    public static void remove() {
        remove(TRACE_ID);
    }

    public static void remove(String key) {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if (map != null) {
            map.remove(key);

        }
    }

    public static void clearTrace() {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if (map != null) {
            map.clear();
            TRANSMITTABLE_THREAD_LOCAL.remove();
        }
    }

    public static String generateDefaultRandomSed() {
        return MwIdGeneratorUtil.uuid2();
    }

    public static String generateRandomSed(String str) {
        return str + "_" + generateDefaultRandomSed();
    }

    public static String generateDefaultTraceLogIdPrefix() {
        return MwJdk8DateUtil.getNowDate2String("yyyyMMdd_HHmmssSSS");
    }

    public static String generateTraceLogIdPrefix(String appId) {
        return appId + "_" + MwJdk8DateUtil.getNowDate2String("yyyyMMdd_HHmmssSSS");
    }
}
