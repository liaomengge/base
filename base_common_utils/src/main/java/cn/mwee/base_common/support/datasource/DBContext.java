package cn.mwee.base_common.support.datasource;

import cn.mwee.base_common.support.datasource.enums.DbType;

/**
 * Created by liaomengge on 16/4/11.
 */

public class DBContext {

    private static final ThreadLocal<DbType> tlDbKey = new ThreadLocal<>();

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