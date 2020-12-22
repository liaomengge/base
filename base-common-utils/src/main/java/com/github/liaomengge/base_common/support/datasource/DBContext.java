package com.github.liaomengge.base_common.support.datasource;

import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;

/**
 * Created by liaomengge on 16/4/11.
 */

public class DBContext {

    private static final ThreadLocal<DbType> tlDbKey = LyThreadLocalUtil.getThreadLocal();

    public static DbType getDBKey() {
        DbType tlDbType = tlDbKey.get();
        return tlDbType == null ? DbType.MASTER : tlDbType;
    }

    public static void setDBKey(DbType dbKey) {
        if (dbKey == null) {
            throw new IllegalArgumentException("数据源类型不能为空!!!");

        }
        tlDbKey.set(dbKey);
    }

    public static void clearDBKey() {
        tlDbKey.remove();
    }
}