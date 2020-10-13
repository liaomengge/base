package com.github.liaomengge.base_common.base.mybatis.aspect;

import com.github.liaomengge.base_common.support.datasource.StringDBContext;
import com.github.liaomengge.base_common.support.datasource.annotation.*;
import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2019/7/5.
 */
public class MybatisAdvice implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = 4912498910040052222L;

    private static final List<String> writeMethod = Lists.newArrayList("update", "modify", "edit", "set",
            "cancel", "create", "add", "insert", "save", "del", "delete");

    private final Map<DbType, String> dbTypeMap;
    private final String dsKeys;
    private final boolean defaultMaster;

    public MybatisAdvice(String dsKeys, boolean defaultMaster) {
        this.dsKeys = dsKeys;
        this.defaultMaster = defaultMaster;
        dbTypeMap = Maps.newHashMap();
        DbType[] dbTypes = new DbType[]{DbType.MASTER, DbType.SLAVE, DbType.COLD, DbType.HISTORY, DbType.OTHER};
        List<String> dbKeyList = SPLITTER.splitToList(this.dsKeys);
        for (int i = 0; i < dbKeyList.size() && i < dbTypes.length; i++) {
            dbTypeMap.put(dbTypes[i], dbKeyList.get(i));
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        try {
            //方法上的@Master/@Slave注解优先
            if (this.defaultMaster) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.MASTER));
            } else {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.SLAVE));
            }

            if (method.isAnnotationPresent(Master.class)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.MASTER));
                return invocation.proceed();
            }

            if (method.isAnnotationPresent(Slave.class)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.SLAVE));
                return invocation.proceed();
            }

            if (method.isAnnotationPresent(Cold.class)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.COLD));
                return invocation.proceed();
            }

            if (method.isAnnotationPresent(History.class)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.HISTORY));
                return invocation.proceed();
            }

            if (method.isAnnotationPresent(Other.class)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.OTHER));
                return invocation.proceed();
            }

            //然后根据方法前缀
            String methodName = method.getName();
            if (isWriteMethod(methodName)) {
                StringDBContext.setDBKey(dbTypeMap.get(DbType.MASTER));
            }
            return invocation.proceed();
        } finally {
            StringDBContext.clearDBKey();
        }
    }

    private boolean isWriteMethod(String methodName) {
        for (String prefix : writeMethod) {
            if (methodName.toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
