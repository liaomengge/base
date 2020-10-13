package com.github.liaomengge.base_common.support.proxy;


import java.util.Objects;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * Created by liaomengge on 2020/5/20.
 */
public abstract class CglibDynamicProxy implements MethodInterceptor {

    private Object target;
    private Class<?> targetClass;

    public Object newProxy(Object target) {
        return newProxy(target, true);
    }

    public Object newProxy(Class<?> targetClass) {
        return newProxy(targetClass, true);
    }

    public Object newProxy(Object target, boolean useCache) {
        return newProxy(target, null, useCache);
    }

    public Object newProxy(Class<?> targetClass, boolean useCache) {
        return newProxy(null, targetClass, useCache);
    }

    protected Object newProxy(Object target, Class<?> targetClass, boolean useCache) {
        this.target = target;
        this.targetClass = targetClass;
        Enhancer enhancer = new Enhancer();
        Class<?> superClass = Objects.nonNull(target) ? target.getClass() : targetClass;
        enhancer.setSuperclass(superClass);
        enhancer.setCallback(this);
        enhancer.setUseCache(useCache);
        return enhancer.create();
    }
}
