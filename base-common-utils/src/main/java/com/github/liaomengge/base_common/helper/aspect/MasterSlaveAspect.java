package com.github.liaomengge.base_common.helper.aspect;

import com.github.liaomengge.base_common.support.datasource.DBContext;
import com.github.liaomengge.base_common.support.datasource.annotation.*;
import com.github.liaomengge.base_common.support.datasource.enums.DbType;
import com.google.common.collect.Lists;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by liaomengge on 16/8/29.
 * 自定义注册切换切面
 */
@Aspect
public class MasterSlaveAspect {

    private static final List<String> writeMethod = Lists.newArrayList("update", "modify", "edit", "set",
            "cancel", "create", "add", "insert", "save", "del", "delete");

    private boolean defaultMaster;

    public MasterSlaveAspect() {
        this.defaultMaster = false;
    }

    public MasterSlaveAspect(boolean defaultMaster) {
        this.defaultMaster = defaultMaster;
    }

    @Pointcut("this(tk.mybatis.mapper.common.Mapper)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        //方法上的@Master/@Slave注解优先
        if (this.defaultMaster) {
            DBContext.setDBKey(DbType.MASTER);
        } else {
            DBContext.setDBKey(DbType.SLAVE);
        }

        if (method.isAnnotationPresent(Master.class)) {
            DBContext.setDBKey(DbType.MASTER);
            return;
        }

        if (method.isAnnotationPresent(Slave.class)) {
            DBContext.setDBKey(DbType.SLAVE);
            return;
        }

        if (method.isAnnotationPresent(Cold.class)) {
            DBContext.setDBKey(DbType.COLD);
            return;
        }

        if (method.isAnnotationPresent(History.class)) {
            DBContext.setDBKey(DbType.HISTORY);
            return;
        }

        if (method.isAnnotationPresent(Other.class)) {
            DBContext.setDBKey(DbType.OTHER);
            return;
        }

        //然后根据方法前缀
        String methodName = joinPoint.getSignature().getName();
        if (isWriteMethod(methodName)) {
            DBContext.setDBKey(DbType.MASTER);
        }
    }

    @After("pointcut()")
    public void doAfter() {
        DBContext.clearDBKey();
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
