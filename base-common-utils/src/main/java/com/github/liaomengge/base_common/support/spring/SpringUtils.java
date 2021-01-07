package com.github.liaomengge.base_common.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2019/10/17.
 */
public class SpringUtils implements ApplicationContextAware {

    private static Environment staticEnvironment;
    private static ApplicationContext staticApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        staticApplicationContext = applicationContext;
        staticEnvironment = applicationContext.getEnvironment();
    }

    public static <T> T getBean(Class<T> clz) {
        if (staticApplicationContext == null) {
            return null;
        }
        return staticApplicationContext.getBean(clz);
    }

    public static <T> T getBean(String beanName) {
        if (staticApplicationContext == null) {
            return null;
        }
        return (T) staticApplicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clz) {
        if (staticApplicationContext == null) {
            return null;
        }
        return staticApplicationContext.getBean(beanName, clz);
    }

    public static void publishEvent(ApplicationEvent event) {
        if (staticApplicationContext == null) {
            return;
        }
        staticApplicationContext.publishEvent(event);
    }

    public static ApplicationContext getApplicationContext() {
        return staticApplicationContext;
    }

    public static Environment getEnvironment() {
        return staticEnvironment;
    }

    public static String getApplicationName() {
        return getEnvironment().getProperty("spring.application.name");
    }

    public static String getContextPath() {
        return getEnvironment().getProperty("server.servlet.context-path", "/");
    }
}
