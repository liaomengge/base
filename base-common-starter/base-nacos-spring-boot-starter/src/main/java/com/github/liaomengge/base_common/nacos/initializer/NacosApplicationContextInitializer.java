package com.github.liaomengge.base_common.nacos.initializer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.github.liaomengge.base_common.nacos.NacosProperties;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liaomengge on 2020/8/17.
 */
public class NacosApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LyLogger.getInstance(NacosApplicationContextInitializer.class);

    private AtomicBoolean switchTraffic = new AtomicBoolean(false);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                ConfigurableEnvironment environment = applicationContext.getEnvironment();
                if (bean instanceof NacosDiscoveryProperties) {
                    NacosDiscoveryProperties nacosDiscoveryProperties = (NacosDiscoveryProperties) bean;

                    NacosProperties nacosProperties = applicationContext.getBean(NacosProperties.class);
                    if (!nacosProperties.getReceiveTraffic().isEnabled() && switchTraffic.compareAndSet(false, true)) {
                        nacosDiscoveryProperties.setInstanceEnabled(false);
                        log.info("set init instance enabled[false]...");
                    }
                    Map<String, String> metaMap = nacosDiscoveryProperties.getMetadata();
                    metaMap.put(NacosConst.MetadataConst.SPRING_BOOT_VERSION,
                            SpringBootVersion.getVersion());
                    metaMap.put(NacosConst.MetadataConst.SPRING_APPLICATION_NAME,
                            environment.getProperty(NacosConst.MetadataConst.SPRING_APPLICATION_NAME));
                    metaMap.put(NacosConst.MetadataConst.APPLICATION_CONTEXT_PATH,
                            environment.getProperty(NacosConst.MetadataConst.APPLICATION_CONTEXT_PATH, "/"));
                    metaMap.put(NacosConst.MetadataConst.APPLICATION_SERVER_PORT,
                            environment.getProperty(NacosConst.MetadataConst.APPLICATION_SERVER_PORT));
                }
                return bean;
            }
        });
    }
}
