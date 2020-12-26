package com.github.liaomengge.base_common.utils.aop;

import lombok.experimental.UtilityClass;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import java.lang.annotation.Annotation;

/**
 * Created by liaomengge on 2019/12/20.
 */
@UtilityClass
public class LyAnnotationPointcutUtil {

    /**
     * 类和方法上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationClassOrMethodPointcut(Class<? extends Annotation> annotationType) {
        Pointcut classPointcut = buildAnnotationClassPointcut(annotationType);
        Pointcut methodPointcut = buildAnnotationMethodPointcut(annotationType);

        ComposablePointcut pointcut = new ComposablePointcut(classPointcut);
        return pointcut.union(methodPointcut);
    }

    /**
     * 类上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationClassPointcut(Class<? extends Annotation> annotationType) {
        return new AnnotationMatchingPointcut(annotationType, true);
    }

    /**
     * 方法上匹配的注解
     *
     * @param annotationType
     * @return
     */
    public Pointcut buildAnnotationMethodPointcut(Class<? extends Annotation> annotationType) {
        return new AnnotationMatchingPointcut(null, annotationType, true);
    }
}
