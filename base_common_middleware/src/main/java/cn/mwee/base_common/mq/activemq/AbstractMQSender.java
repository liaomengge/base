package cn.mwee.base_common.mq.activemq;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import org.slf4j.Logger;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * Created by liaomengge on 17/9/15.
 */
public abstract class AbstractMQSender {

    protected static final Logger logger = MwLogger.getInstance(AbstractMQSender.class);

    public abstract void convertAndSend(String queueName, Object message);

    public abstract void convertAndSend(String queueName, Object message, MessagePostProcessor messagePostProcessor);
}
