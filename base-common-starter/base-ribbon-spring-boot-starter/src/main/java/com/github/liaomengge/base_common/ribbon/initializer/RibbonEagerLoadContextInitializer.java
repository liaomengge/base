package com.github.liaomengge.base_common.ribbon.initializer;

import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.netflix.ribbon.RibbonApplicationContextInitializer;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by liaomengge on 2020/12/22.
 */
public class RibbonEagerLoadContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RibbonApplicationContextInitializer) {
                    SpringClientFactory springClientFactory = applicationContext.getBean(SpringClientFactory.class);
                    FeignClientManager feignClientManager = applicationContext.getBean(FeignClientManager.class);
                    return new RibbonApplicationContextInitializer(springClientFactory,
                            Lists.newArrayList(feignClientManager.getFeignTargetMap().keySet()));
                }
                return bean;
            }
        });
    }
}
