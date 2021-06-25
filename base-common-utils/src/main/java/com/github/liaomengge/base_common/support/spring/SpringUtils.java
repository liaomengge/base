package com.github.liaomengge.base_common.support.spring;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Created by liaomengge on 2019/10/17.
 */
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext staticApplicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        staticApplicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (staticApplicationContext == null) {
            return null;
        }
        return staticApplicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName) {
        if (staticApplicationContext == null) {
            return null;
        }
        return (T) staticApplicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        if (staticApplicationContext == null) {
            return null;
        }
        return staticApplicationContext.getBean(beanName, clazz);
    }

    public static <T> ObjectProvider<T> getBeanProvider(Class<T> clazz) {
        if (staticApplicationContext == null) {
            return null;
        }
        return staticApplicationContext.getBeanProvider(clazz);
    }

    public static void registerBean(String beanName, String beanClassName) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClassName);
        registerBean(beanName, definitionBuilder.getBeanDefinition());
    }

    public static void registerBean(Class<?> beanClass) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        registerBean(beanClass.getTypeName(), definitionBuilder.getBeanDefinition());
    }

    public static void registerBean(String beanName, Class<?> beanClass) {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        registerBean(beanName, definitionBuilder.getBeanDefinition());
    }

    public static void registerBean(String beanName, BeanDefinition beanDefinition) {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public static void unregisterBean(String beanName) {
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        beanFactory.removeBeanDefinition(beanName);
    }

    public static void unregisterBean(Class<?> beanClass) {
        unregisterBean(beanClass.getTypeName());
    }

    public static boolean isExistBean(String beanName) {
        return staticApplicationContext.containsBean(beanName);
    }

    public static boolean isExistBean(Class<?> beanClass) {
        try {
            Map<String, ?> beanMap = staticApplicationContext.getBeansOfType(beanClass);
            return MapUtils.isNotEmpty(beanMap);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isUniqueBean(Class<?> beanClass) {
        try {
            Map<String, ?> beanMap = staticApplicationContext.getBeansOfType(beanClass);
            return MapUtils.size(beanMap) == 1;
        } catch (Exception e) {
            return false;
        }
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
        return staticApplicationContext.getEnvironment();
    }

    public static DefaultListableBeanFactory getBeanFactory() {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) staticApplicationContext;
        return (DefaultListableBeanFactory) context.getBeanFactory();
    }

    public static String getApplicationName() {
        return getEnvironment().getProperty("spring.application.name");
    }

    public static String getContextPath() {
        return getEnvironment().getProperty("server.servlet.context-path", "/");
    }
}
