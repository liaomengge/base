package cn.mwee.base_common.mq;

import cn.mwee.base_common.mq.activemq.ActiveMQAutoConfiguration;
import cn.mwee.base_common.mq.rabbitmq.RabbitMQAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/5/5.
 */
@Configuration
@Import({ActiveMQAutoConfiguration.class, RabbitMQAutoConfiguration.class})
public class MQAutoConfiguration {
}
