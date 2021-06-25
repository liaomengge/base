package com.github.liaomengge.base_common.framework.registry;

import com.github.liaomengge.base_common.framework.util.FrameworkPackageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.List;

/**
 * Created by liaomengge on 2019/3/29.
 */
@Slf4j
public class FrameworkBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {
    
    private static final String FRAMEWORK_PKG = "com.github.liaomengge.service.base_framework";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        int registeredNum = scanner.scan(FRAMEWORK_PKG);
        log.info("base framework registered number ===> [{}]", registeredNum);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        List<String> basePackages = AutoConfigurationPackages.get(configurableListableBeanFactory);
        FrameworkPackageUtil.setBasePackages(basePackages);
    }
}
