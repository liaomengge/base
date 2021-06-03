package com.github.liaomengge.base_common.utils.annotation;

import lombok.experimental.UtilityClass;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by liaomengge on 2020/8/20.
 */
@UtilityClass
public class LyAnnotationUtil {

    public <A extends Annotation> A getAnnotation(ProceedingJoinPoint joinPoint, Class<A> annotationType) {
        Method specificMethod = getSpecificMethod(joinPoint);
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if (null != annotation) {
            return annotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }

    public <A extends Annotation> A getAnnotation(MethodInvocation invocation, Class<A> annotationType) {
        Method specificMethod = getSpecificMethod(invocation);
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if (null != annotation) {
            return annotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }

    public <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        Method specificMethod = getSpecificMethod(method);
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if (null != annotation) {
            return annotation;
        }
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }


    public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotationType) {
        // 先找方法，再找方法上的类
        boolean isMethodAnnotated = AnnotatedElementUtils.isAnnotated(method, annotationType);
        if (isMethodAnnotated) {
            return true;
        }
        // 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
        Class<?> targetClass = method.getDeclaringClass();
        return AnnotatedElementUtils.isAnnotated(targetClass, annotationType);
    }

    /**
     * 获取Proxy实际执行的方法
     *
     * @param joinPoint
     * @return
     */
    public Method getSpecificMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = target != null ? AopProxyUtils.ultimateTargetClass(joinPoint.getTarget()) : null;
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        //获取第一个实际对象的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return BridgeMethodResolver.findBridgedMethod(specificMethod);
    }

    /**
     * 获取Proxy实际执行的方法
     *
     * @param invocation
     * @return
     */
    public Method getSpecificMethod(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        Class<?> targetClass = target != null ? AopProxyUtils.ultimateTargetClass(target) : null;
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        //获取第一个实际对象的方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return BridgeMethodResolver.findBridgedMethod(specificMethod);
    }

    private Method getSpecificMethod(Method method) {
        Class<?> targetClass = method.getDeclaringClass();
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return BridgeMethodResolver.findBridgedMethod(specificMethod);
    }
}
