package com.github.liaomengge.base_common.support.proxy;


import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

import java.util.Objects;

/**
 * Created by liaomengge on 2020/5/20.
 * 可参考spring#ProxyFactory
 */
public class CglibDynamicProxyFactory {

    public static <T> T getProxy(Object target, Callback callback) {
        return getProxy(target, true, callback);
    }

    public static <T> T getProxy(Class<?> targetClass, Callback callback) {
        return getProxy(targetClass, true, callback);
    }

    public static <T> T getProxy(Object target, boolean useCache, Callback callback) {
        return getProxy(target, null, useCache, callback);
    }

    public static <T> T getProxy(Class<?> targetClass, boolean useCache, Callback callback) {
        return getProxy(null, targetClass, useCache, callback);
    }

    public static <T> T getProxy(Object target, Class<?> targetClass, boolean useCache, Callback callback) {
        return new CglibDynamicProxyConfig(target, targetClass, callback).getProxy(useCache);
    }

    protected static class CglibDynamicProxyConfig {
        private Object target;
        private Class<?> targetClass;
        private Callback callback;

        public CglibDynamicProxyConfig(Object target, Callback callback) {
            this.target = target;
            this.callback = callback;
        }

        public CglibDynamicProxyConfig(Class<?> targetClass, Callback callback) {
            this.targetClass = targetClass;
            this.callback = callback;
        }

        public CglibDynamicProxyConfig(Object target, Class<?> targetClass, Callback callback) {
            this.target = target;
            this.targetClass = targetClass;
            this.callback = callback;
        }

        public <T> T getProxy() {
            return getProxy(true);
        }

        public <T> T getProxy(boolean useCache) {
            if (Objects.isNull(this.target) && Objects.isNull(this.targetClass)) {
                throw new IllegalArgumentException("target & target class both null");
            }
            return (T) createEnhancer(useCache).create();
        }

        protected Enhancer createEnhancer(boolean useCache) {
            Enhancer enhancer = new Enhancer();
            Class<?> superClass = Objects.nonNull(this.target) ? this.target.getClass() : this.targetClass;
            enhancer.setSuperclass(superClass);
            enhancer.setCallback(this.callback);
            enhancer.setUseCache(useCache);
            return enhancer;
        }
    }
}
