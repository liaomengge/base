package cn.ly.service.base_framework.common.util;

import cn.ly.base_common.utils.number.MwNumberUtil;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2018/11/22.
 */
@UtilityClass
public class TimeThreadLocalUtil {

    private final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    private TimeThreadLocalUtil() {
    }

    public void set(long time) {
        threadLocal.set(time);
    }

    public long get() {
        return MwNumberUtil.getLongValue(threadLocal.get());
    }

    public void remove() {
        threadLocal.remove();
    }
}
