package com.github.liaomengge.base_common.support.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * Created by liaomengge on 2020/5/20.
 * 可参考spring#ProxyFactory
 */
public class JdkDynamicProxyFactory {

    public static <T> T getProxy(Object target, InvocationHandler invocationHandler) {
        return getProxy(target, null, invocationHandler);
    }

    public static <T> T getProxy(Class<?> targetClass, InvocationHandler invocationHandler) {
        return getProxy(null, targetClass, invocationHandler);
    }

    public static <T> T getProxy(Object target, Class<?> targetClass, InvocationHandler invocationHandler) {
        return new JdkDynamicProxyConfig(target, targetClass, invocationHandler).getProxy();
    }

    protected static class JdkDynamicProxyConfig {
        private Object target;
        private Class<?> targetClass;
        private InvocationHandler invocationHandler;

        public JdkDynamicProxyConfig(Object target, InvocationHandler invocationHandler) {
            this.target = target;
            this.invocationHandler = invocationHandler;
        }

        public JdkDynamicProxyConfig(Class<?> targetClass, InvocationHandler invocationHandler) {
            this.targetClass = targetClass;
            this.invocationHandler = invocationHandler;
        }

        public JdkDynamicProxyConfig(Object target, Class<?> targetClass, InvocationHandler invocationHandler) {
            this.target = target;
            this.targetClass = targetClass;
            this.invocationHandler = invocationHandler;
        }

        public <T> T getProxy() {
            if (Objects.isNull(this.target) && Objects.isNull(this.targetClass)) {
                throw new IllegalArgumentException("target & target class both null");
            }
            Class<?> targetClazz = Objects.nonNull(this.target) ? this.target.getClass() : this.targetClass;
            return (T) Proxy.newProxyInstance(targetClazz.getClassLoader(), getInterfaces(targetClazz),
                    this.invocationHandler);
        }

        private Class<?>[] getInterfaces(Class<?> clazz) {
            if (clazz.isInterface()) {
                return new Class[]{clazz};
            }
            return clazz.getInterfaces();
        }
    }
}
