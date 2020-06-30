package cn.ly.base_common.mq.activemq.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/23.
 */
@Configuration
public class ActiveMQQueueConfigBeanRegistryConfiguration {

    @Bean
    public static ActiveMQQueueConfigBeanDefinitionRegistry activeMQQueueConfigBeanDefinitionRegistry() {
        return new ActiveMQQueueConfigBeanDefinitionRegistry();
    }
}
