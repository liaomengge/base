package cn.ly.base_common.mq.rabbitmq;

import cn.ly.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 17/1/4.
 */
public abstract class AbstractMQSender {

    protected static final Logger logger = LyLogger.getInstance(AbstractMQSender.class);

    public abstract void convertAndSend(int routeKeyHash, Object object);

    public abstract void convertAndSend(String routingKey, Object object);

    public abstract void convertAndSend(String exchangeName, String routingKey, Object object);

    public abstract void convertAndSend2(String queueName, Object object);

    public abstract void convertAndSend2(String exchangeName, String queueName, Object object);
}