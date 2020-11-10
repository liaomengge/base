package com.github.liaomengge.base_common.multi.shardingsphere.aspect;

import com.github.liaomengge.base_common.multi.shardingsphere.annotation.HintMaster;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.masterslave.route.engine.impl.MasterVisitedManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * Created by liaomengge on 2019/9/16.
 * Sharding jdbc切面
 */
@Aspect
public class HintMasterAspect {

    @Pointcut("this(tk.mybatis.mapper.common.Mapper)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object setMasterOnly(ProceedingJoinPoint joinPoint) throws Throwable {
        if (HintManager.isMasterRouteOnly() || MasterVisitedManager.isMasterVisited()) {
            return joinPoint.proceed();
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            if (method.isAnnotationPresent(HintMaster.class)) {
                HintManager.getInstance().setMasterRouteOnly();
            }
            return joinPoint.proceed();
        } finally {
            HintManager.clear();
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Around("@annotation(transactional)")
    public Object setTransactionalMasterOnly(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        try {
            if (HintManager.isMasterRouteOnly() || MasterVisitedManager.isMasterVisited()) {
                return joinPoint.proceed();
            }

            HintManager.getInstance().setMasterRouteOnly();
            return joinPoint.proceed();
        } finally {
            HintManager.clear();
        }
    }
}
