package com.github.liaomengge.service.base_framework.common.util;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2018/11/22.
 */
@UtilityClass
public class TimeThreadLocalUtil {

    private final ThreadLocal<Long> TIME_THREAD_LOCAL = LyThreadLocalUtil.getNamedThreadLocal("elapsed-time");

    public void set(long time) {
        TIME_THREAD_LOCAL.set(time);
    }

    public long get() {
        return LyNumberUtil.getLongValue(TIME_THREAD_LOCAL.get());
    }

    public void remove() {
        TIME_THREAD_LOCAL.remove();
    }
}
