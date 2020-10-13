package com.github.liaomengge.base_common.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * Created by liaomengge on 2019/10/17.
 */
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringUtils.context = context;
    }

    public static <T> T getBean(Class<T> clz) {
        if (context == null) {
            return null;
        }
        return context.getBean(clz);
    }

    public static <T> T getBean(String beanName) {
        if (context == null) {
            return null;
        }
        return (T) context.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clz) {
        if (context == null) {
            return null;
        }
        return context.getBean(beanName, clz);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void publishEvent(ApplicationEvent event) {
        if (context == null) {
            return;
        }
        context.publishEvent(event);
    }
}
