package cn.mwee.base_common.utils.aop;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * Created by liaomengge on 6/6/16.
 */
@UtilityClass
public class MwAopTargetUtil {

    private final Logger logger = MwLogger.getInstance(MwAopTargetUtil.class);

    /**
     * 通过cglib获取代理对象(必须开启exposeProxy=true)
     *
     * @param target
     * @param <T>
     * @return
     */
    public <T> T getProxy(T target) {
        if (AopUtils.isAopProxy(target)) {
            return target;//是代理对象
        }

        try {
            Object proxy = AopContext.currentProxy();
            if (proxy.getClass().getSuperclass().equals(proxy.getClass())) { // 有时出现currentProxy和t类型不一致, 这里做一下判断
                return (T) proxy;
            }
        } catch (IllegalStateException e) {
            logger.warn("exposeProxy必须配置true", e);
        }
        return target;
    }

    /**
     * 获取目标对象
     *
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    public <T> T getTarget(T proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;//不是代理对象
        }

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return (T) getJdkDynamicProxyTargetObject(proxy);
        } else { //cglib
            return (T) getCglibProxyTargetObject(proxy);
        }
    }

    private Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

        return target;
    }

    private Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

        return target;
    }
}
