package cn.ly.base_common.mq.activemq.processor;

import lombok.Data;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * MQ延时投递处理器（注：ActiveMQ的配置文件中, 要配置schedulerSupport="true", 否则不起作用）
 * by: 杨俊明 2016-06-16
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
