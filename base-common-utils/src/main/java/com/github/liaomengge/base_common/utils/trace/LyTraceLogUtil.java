package com.github.liaomengge.base_common.utils.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.misc.LyIdGeneratorUtil;
import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaomengge on 17/5/25.
 */
@UtilityClass
public class LyTraceLogUtil {

    public final String TRACE_ID = "x-base-trace-id";

    private final TransmittableThreadLocal<Map<String, String>> TRANSMITTABLE_THREAD_LOCAL =
            LyThreadLocalUtil.getTransmittableThreadLocal();

    public void put(String val) {
        put(TRACE_ID, val);
    }

    public void put(String key, String val) {
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

    public String get() {
        return get(TRACE_ID);
    }

    public String get(String key) {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        }
        return null;
    }

    public void remove() {
        remove(TRACE_ID);
    }

    public void remove(String key) {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if (map != null) {
            map.remove(key);

        }
    }

    public void clearTrace() {
        Map<String, String> map = TRANSMITTABLE_THREAD_LOCAL.get();
        if (map != null) {
            map.clear();
            TRANSMITTABLE_THREAD_LOCAL.remove();
        }
    }

    public String generateDefaultRandomSed() {
        return LyIdGeneratorUtil.uuid2();
    }

    public String generateRandomSed(String str) {
        return str + "_" + generateDefaultRandomSed();
    }

    public String generateDefaultTraceLogIdPrefix() {
        return LyJdk8DateUtil.getNowDate2String("yyyyMMdd_HHmmssSSS");
    }

    public String generateTraceLogIdPrefix(String appId) {
        return appId + "_" + LyJdk8DateUtil.getNowDate2String("yyyyMMdd_HHmmssSSS");
    }
}
