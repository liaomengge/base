package com.github.liaomengge.base_common.eureka.initializer;

import com.github.liaomengge.base_common.eureka.EurekaProperties;
import com.github.liaomengge.base_common.eureka.consts.EurekaConst;
import com.github.liaomengge.base_common.eureka.decorator.EurekaServiceRegistryDecorator;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringBootVersion;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liaomengge on 2020/8/15.
 */
public class EurekaApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LyLogger.getInstance(EurekaApplicationContextInitializer.class);

    private AtomicBoolean switchFlow = new AtomicBoolean(false);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                ConfigurableEnvironment environment = applicationContext.getEnvironment();
                if (bean instanceof EurekaServiceRegistry) {
                    EurekaServiceRegistry serviceRegistry = (EurekaServiceRegistry) bean;
                    return new EurekaServiceRegistryDecorator(environment, serviceRegistry);
                }
                if (bean instanceof EurekaInstanceConfigBean) {
                    EurekaInstanceConfigBean instanceConfigBean = (EurekaInstanceConfigBean) bean;

                    EurekaProperties eurekaProperties = applicationContext.getBean(EurekaProperties.class);
                    if (eurekaProperties.getPull().isEnabled() && switchFlow.compareAndSet(false, true)) {
                        instanceConfigBean.setInitialStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
                        log.info("set init status[OUT_OF_SERVICE] and meta data...");
                    }

                    Map<String, String> metaMap = instanceConfigBean.getMetadataMap();
                    metaMap.put(EurekaConst.SPRING_BOOT_VERSION,
                            SpringBootVersion.getVersion());
                    metaMap.put(EurekaConst.SPRING_APPLICATION_NAME,
                            environment.getProperty(EurekaConst.SPRING_APPLICATION_NAME));
                    metaMap.put(EurekaConst.SPRING_APPLICATION_CONTEXT_PATH,
                            environment.getProperty(EurekaConst.SPRING_APPLICATION_CONTEXT_PATH, "/"));
                    metaMap.put(EurekaConst.SPRING_APPLICATION_SERVER_PORT,
                            environment.getProperty(EurekaConst.SPRING_APPLICATION_SERVER_PORT));
                }
                return bean;
            }
        });
    }
}
