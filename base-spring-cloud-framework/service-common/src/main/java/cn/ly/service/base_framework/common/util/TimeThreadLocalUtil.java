package cn.ly.service.base_framework.common.util;

import cn.ly.base_common.utils.number.MwNumberUtil;

/**
 * Created by liaomengge on 2018/11/22.
 */
public final class TimeThreadLocalUtil {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    private TimeThreadLocalUtil() {
    }

    public static void set(long time) {
        threadLocal.set(time);
    }

    public static long get() {
        return MwNumberUtil.getLongValue(threadLocal.get());
    }

    public static void remove() {
        threadLocal.remove();
    }
}
