package com.github.liaomengge.base_common.mq.rabbitmq.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/23.
 */
@Configuration
public class RabbitMQQueueConfigBeanRegistryConfiguration {

    @Bean
    public static RabbitMQQueueConfigBeanDefinitionRegistry rabbitMQQueueConfigBeanDefinitionRegistry() {
        return new RabbitMQQueueConfigBeanDefinitionRegistry();
    }
}
