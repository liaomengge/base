package cn.ly.base_common.support.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by liaomengge on 2020/5/20.
 */
public abstract class JdkDynamicProxy implements InvocationHandler {

    private Object target;

    public Object newProxy(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }
}
