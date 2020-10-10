package cn.ly.base_common.mq.activemq.processor;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.MessagePostProcessor;

import lombok.Data;

/**
 * MQ延时投递处理器（注：ActiveMQ的配置文件中, 要配置schedulerSupport="true", 否则不起作用）
 */
@Data
public class SchedulerMessagePostProcessor implements MessagePostProcessor {

    private long delay = 0L;

    private String corn = null;

    public SchedulerMessagePostProcessor(long delay) {
        this.delay = delay;
    }

    public SchedulerMessagePostProcessor(String cron) {
        this.corn = cron;
    }

    @Override
    public Message postProcessMessage(Message message) throws JMSException {
        if (delay > 0) {
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
        }
        if (!StringUtils.isBlank(corn)) {
            message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, corn);
        }
        return message;
    }
}
