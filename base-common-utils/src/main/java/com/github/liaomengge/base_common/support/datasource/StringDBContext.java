package com.github.liaomengge.base_common.support.datasource;

import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class StringDBContext {

    private static ThreadLocal<String> STRING_THREAD_LOCAL = LyThreadLocalUtil.getThreadLocal();

    public static String getDBKey() {
        return STRING_THREAD_LOCAL.get();
    }

    public static void setDBKey(String dbKey) {
        if (dbKey == null) {
            throw new IllegalArgumentException("数据源类型不能为空!!!");

        }
        STRING_THREAD_LOCAL.set(dbKey);
    }

    public static void clearDBKey() {
        STRING_THREAD_LOCAL.remove();
    }
}
