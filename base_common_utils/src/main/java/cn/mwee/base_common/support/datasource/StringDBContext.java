package cn.mwee.base_common.support.datasource;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class StringDBContext {

    private static final ThreadLocal<String> tlDbKey = new ThreadLocal<>();

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
