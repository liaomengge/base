package com.github.liaomengge.base_common.nacos.initializer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.github.liaomengge.base_common.nacos.NacosProperties;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.nacos.decorator.NacosServiceRegistryDecorator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * Created by liaomengge on 2020/8/17.
 */
public class NacosApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                ConfigurableEnvironment environment = applicationContext.getEnvironment();
                if (bean instanceof NacosServiceRegistry) {
                    NacosServiceRegistry serviceRegistry = (NacosServiceRegistry) bean;
                    NacosProperties nacosProperties =
                            applicationContext.getBean(NacosProperties.class);
                    NacosDiscoveryProperties nacosDiscoveryProperties =
                            applicationContext.getBean(NacosDiscoveryProperties.class);
                    NacosServiceManager nacosServiceManager = applicationContext.getBean(NacosServiceManager.class);
                    return new NacosServiceRegistryDecorator(nacosProperties,
                            environment, serviceRegistry, nacosDiscoveryProperties, nacosServiceManager);
                }
                if (bean instanceof NacosDiscoveryProperties) {
                    /**
                     * 2.2.0之前的版本，可以直接在这里设置instanceEnabled属性为false即可
                     * 2.2.0版本NacosDiscoveryProperties的instanceEnabled属性删除了，需要重写Instance的enabled属性
                     */
                    NacosDiscoveryProperties nacosDiscoveryProperties = (NacosDiscoveryProperties) bean;
                    Map<String, String> metaMap = nacosDiscoveryProperties.getMetadata();
                    metaMap.put(NacosConst.SPRING_BOOT_VERSION,
                            SpringBootVersion.getVersion());
                    metaMap.put(NacosConst.SPRING_APPLICATION_NAME,
                            environment.getProperty(NacosConst.SPRING_APPLICATION_NAME));
                    metaMap.put(NacosConst.SPRING_APPLICATION_CONTEXT_PATH,
                            environment.getProperty(NacosConst.SPRING_APPLICATION_CONTEXT_PATH, "/"));
                    metaMap.put(NacosConst.SPRING_APPLICATION_SERVER_PORT,
                            environment.getProperty(NacosConst.SPRING_APPLICATION_SERVER_PORT));
                }
                return bean;
            }
        });
    }
}
