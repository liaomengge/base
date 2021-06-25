package com.github.liaomengge.base_common.mq.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * Created by liaomengge on 17/9/15.
 */
public abstract class AbstractMQSender {

    protected static final Logger log = LoggerFactory.getLogger(AbstractMQSender.class);

    public abstract void convertAndSend(String queueName, Object message);

    public abstract void convertAndSend(String queueName, Object message, MessagePostProcessor messagePostProcessor);
}
