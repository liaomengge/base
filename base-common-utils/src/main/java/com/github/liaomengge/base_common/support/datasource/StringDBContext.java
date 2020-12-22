package com.github.liaomengge.base_common.support.datasource;

import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class StringDBContext {

    private static final ThreadLocal<String> tlDbKey = LyThreadLocalUtil.getThreadLocal();

    public static String getDBKey() {
        return tlDbKey.get();
    }

    public static void setDBKey(String dbKey) {
        if (dbKey == null) {
            throw new IllegalArgumentException("数据源类型不能为空!!!");

        }
        tlDbKey.set(dbKey);
    }

    public static void clearDBKey() {
        tlDbKey.remove();
    }
}
