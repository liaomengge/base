package com.github.liaomengge.base_common.support.datasource;

import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;

/**
 * Created by liaomengge on 16/4/11.
 */

public class DBContext {

    private static ThreadLocal<DbType> DB_TYPE_THREAD_LOCAL = LyThreadLocalUtil.getNamedThreadLocal("enum-db-type");

    public static DbType getDBKey() {
        DbType tlDbType = DB_TYPE_THREAD_LOCAL.get();
        return tlDbType == null ? DbType.MASTER : tlDbType;
    }

    public static void setDBKey(DbType dbKey) {
        if (dbKey == null) {
            throw new IllegalArgumentException("数据源类型不能为空!!!");

        }
        DB_TYPE_THREAD_LOCAL.set(dbKey);
    }

    public static void clearDBKey() {
        DB_TYPE_THREAD_LOCAL.remove();
    }
}