package com.github.liaomengge.base_common.utils.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.misc.LyIdGeneratorUtil;
import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 17/5/25.
 */
@UtilityClass
public class LyTraceLogUtil {

    public final String TRACE_ID = "x-base-trace-id";

    private final TransmittableThreadLocal<Map<String, String>> BASE_TRACE_THREAD_LOCAL =
            LyThreadLocalUtil.getNamedTransmittableThreadLocal("base-trace-id");

    public void put(String value) {
        put(TRACE_ID, value);
    }

    public void put(String key, String value) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = BASE_TRACE_THREAD_LOCAL.get();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
            BASE_TRACE_THREAD_LOCAL.set(map);
        }
        map.put(key, value);
    }

    public String get() {
        return get(TRACE_ID);
    }

    public String get(String key) {
        Map<String, String> map = BASE_TRACE_THREAD_LOCAL.get();
        if (Objects.nonNull(map) && Objects.nonNull(key)) {
            return map.get(key);
        }
        return null;
    }

    public void clearTrace() {
        Map<String, String> map = BASE_TRACE_THREAD_LOCAL.get();
        if (Objects.nonNull(map)) {
            map.clear();
        }
        BASE_TRACE_THREAD_LOCAL.remove();
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
