package cn.ly.base_common.framework.registry;

import cn.ly.base_common.utils.log4j2.MwLogger;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * Created by liaomengge on 2019/3/29.
 */
public class FrameworkBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = MwLogger.getInstance(FrameworkBeanDefinitionRegistry.class);

    public static final String FRAMEWORK_PKG = "cn.mwee.service.base_framework";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
        int registeredNum = scanner.scan(FRAMEWORK_PKG);
        logger.info("base framework registered number ===> [{}]", registeredNum);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
