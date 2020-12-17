package com.github.liaomengge.base_common.support.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by liaomengge on 2020/5/20.
 * 可参考spring#ProxyFactory
 */
public abstract class JdkDynamicProxy implements InvocationHandler {

    private Object target;
    private Class<?> targetClass;

    public <T> T newProxy(Object target) {
        this.target = target;
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }

    public <T> T newProxy(Class<?> targetClass) {
        this.targetClass = targetClass;
        return (T) Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), this);
    }
}
