package com.github.liaomengge.base_common.mq.activemq.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/23.
 */
@Configuration(proxyBeanMethods = false)
public class ActiveMQQueueConfigBeanRegistryConfiguration {

    @Bean
    public static ActiveMQQueueConfigBeanDefinitionRegistry activeMQQueueConfigBeanDefinitionRegistry() {
        return new ActiveMQQueueConfigBeanDefinitionRegistry();
    }
}
