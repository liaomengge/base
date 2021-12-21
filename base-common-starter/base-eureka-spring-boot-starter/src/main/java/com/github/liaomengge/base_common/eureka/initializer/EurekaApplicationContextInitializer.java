package com.github.liaomengge.base_common.eureka.initializer;

import com.github.liaomengge.base_common.consts.BaseConst;
import com.github.liaomengge.base_common.eureka.EurekaProperties;
import com.github.liaomengge.base_common.eureka.consts.EurekaConst;
import com.github.liaomengge.base_common.eureka.decorator.EurekaServiceRegistryDecorator;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EurekaApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private AtomicBoolean switchTraffic = new AtomicBoolean(false);

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
                    if (!eurekaProperties.getReceiveTraffic().isEnabled() && switchTraffic.compareAndSet(false, true)) {
                        instanceConfigBean.setInitialStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
                        log.info("set init status[OUT_OF_SERVICE]...");
                    }

                    Map<String, String> metadataMap = instanceConfigBean.getMetadataMap();
                    metadataMap.put(EurekaConst.MetadataConst.SPRING_BOOT_VERSION,
                            SpringBootVersion.getVersion());
                    metadataMap.put(EurekaConst.MetadataConst.SPRING_APPLICATION_NAME,
                            environment.getProperty(EurekaConst.MetadataConst.SPRING_APPLICATION_NAME));
                    metadataMap.put(EurekaConst.MetadataConst.APPLICATION_CONTEXT_PATH,
                            environment.getProperty(EurekaConst.MetadataConst.APPLICATION_CONTEXT_PATH, "/"));
                    metadataMap.put(EurekaConst.MetadataConst.APPLICATION_SERVER_PORT,
                            environment.getProperty(EurekaConst.MetadataConst.APPLICATION_SERVER_PORT));
                    metadataMap.put(EurekaConst.MetadataConst.PRESERVED_REGISTER_TIME,
                            LyJdk8DateUtil.getNowDate2String());
                    metadataMap.put(BaseConst.BASE_FRAMEWORK_VERSION_NAME, BaseConst.BASE_FRAMEWORK_VERSION_VALUE);
                }
                return bean;
            }
        });
    }
}
