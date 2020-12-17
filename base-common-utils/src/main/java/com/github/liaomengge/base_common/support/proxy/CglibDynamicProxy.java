package com.github.liaomengge.base_common.support.proxy;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.Objects;

/**
 * Created by liaomengge on 2020/5/20.
 * 可参考spring#ProxyFactory
 */
public abstract class CglibDynamicProxy implements MethodInterceptor {

    private Object target;
    private Class<?> targetClass;

    public <T> T newProxy(Object target) {
        return newProxy(target, true);
    }

    public <T> T newProxy(Class<T> targetClass) {
        return newProxy(targetClass, true);
    }

    public <T> T newProxy(Object target, boolean useCache) {
        return newProxy(target, null, useCache);
    }

    public <T> T newProxy(Class<T> targetClass, boolean useCache) {
        return newProxy(null, targetClass, useCache);
    }

    protected <T> T newProxy(Object target, Class<T> targetClass, boolean useCache) {
        this.target = target;
        this.targetClass = targetClass;
        Enhancer enhancer = new Enhancer();
        Class<?> superClass = Objects.nonNull(target) ? target.getClass() : targetClass;
        enhancer.setSuperclass(superClass);
        enhancer.setCallback(this);
        enhancer.setUseCache(useCache);
        return (T) enhancer.create();
    }
}
