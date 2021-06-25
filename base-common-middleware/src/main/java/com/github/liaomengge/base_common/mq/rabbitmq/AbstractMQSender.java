package com.github.liaomengge.base_common.mq.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaomengge on 17/1/4.
 */
public abstract class AbstractMQSender {

    protected static final Logger log = LoggerFactory.getLogger(AbstractMQSender.class);

    public abstract void convertAndSend(int routeKeyHash, Object object);

    public abstract void convertAndSend(String routingKey, Object object);

    public abstract void convertAndSend(String exchangeName, String routingKey, Object object);

    public abstract void convertAndSend2(String queueName, Object object);

    public abstract void convertAndSend2(String exchangeName, String queueName, Object object);
}
