package com.github.liaomengge.base_common.mq;

import com.github.liaomengge.base_common.mq.activemq.ActiveMQAutoConfiguration;
import com.github.liaomengge.base_common.mq.rabbitmq.RabbitMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/5/5.
 */
@Configuration(proxyBeanMethods = false)
@Import({ActiveMQAutoConfiguration.class, RabbitMQAutoConfiguration.class})
public class MQAutoConfiguration {
}
