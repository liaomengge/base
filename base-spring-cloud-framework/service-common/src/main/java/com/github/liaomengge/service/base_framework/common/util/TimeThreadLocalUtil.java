package com.github.liaomengge.service.base_framework.common.util;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import com.github.liaomengge.base_common.utils.threadlocal.ThreadLocalUtil;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2018/11/22.
 */
@UtilityClass
public class TimeThreadLocalUtil {

    private final ThreadLocal<Long> threadLocal = ThreadLocalUtil.getThreadLocal();

    public void set(long time) {
        threadLocal.set(time);
    }

    public long get() {
        return LyNumberUtil.getLongValue(threadLocal.get());
    }

    public void remove() {
        threadLocal.remove();
    }
}
