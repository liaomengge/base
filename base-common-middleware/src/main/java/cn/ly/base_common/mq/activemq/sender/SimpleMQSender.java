package cn.ly.base_common.mq.activemq.sender;

import cn.ly.base_common.mq.activemq.domain.QueueConfig;
import cn.ly.base_common.mq.activemq.monitor.DefaultMQMonitor;
import cn.ly.base_common.mq.activemq.processor.TraceMessagePostProcessor;
import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.mq.consts.MetricsConst;
import lombok.NonNull;
import lombok.Setter;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * Created by liaomengge on 17/1/4.
 */
public class SimpleMQSender extends BaseMQSender {

    @Setter
    @NonNull
    private QueueConfig queueConfig;

    public SimpleMQSender(PooledConnectionFactory connectionFactory, DefaultMQMonitor activeMQMonitor) {
        super(connectionFactory, activeMQMonitor);
    }

    public SimpleMQSender(PooledConnectionFactory connectionFactory, MessageConverter messageConverter,
                          DefaultMQMonitor activeMQMonitor) {
        super(connectionFactory, messageConverter, activeMQMonitor);
    }

    public void convertAndSend(Object message) {
        this.convertAndSend(0, message);
    }

    public void convertAndSend(int queueHash, Object message) {
        this.convertAndSend(queueHash, message, new TraceMessagePostProcessor());
    }

    public void convertAndSend(Object message, MessagePostProcessor messagePostProcessor) {
        this.convertAndSend(0, message, messagePostProcessor);
    }

    public void convertAndSend(int queueHash, Object message, MessagePostProcessor messagePostProcessor) {
        String queueName = queueConfig.buildQueueName(queueHash);
        if (StringUtils.isBlank(queueName)) {
            log.error("目标队列为空, 无法发送, 请检查配置！message => " + message.toString());
            return;
        }
        jmsTemplate.convertAndSend(queueName, message, messagePostProcessor);
        mqMonitor.monitorCount(MetricsConst.ENQUEUE_COUNT + "." + queueName);
        if (autoBackup) {
            jmsTemplate.convertAndSend(queueName + MQConst.ActiveMQ.BACKUP_QUEUE_SUFFIX, message, messagePostProcessor);
        }
    }

    @Override
    public void convertAndSend(String queueName, Object message) {
        this.convertAndSend(queueName, message, new TraceMessagePostProcessor());
    }

    @Override
    public void convertAndSend(String queueName, Object message, MessagePostProcessor messagePostProcessor) {
        if (StringUtils.isBlank(queueName)) {
            log.error("目标队列为空, 无法发送, 请检查配置！message => " + message.toString());
            return;
        }
        jmsTemplate.convertAndSend(queueName, message, messagePostProcessor);
        mqMonitor.monitorCount(MetricsConst.ENQUEUE_COUNT + "." + queueName);
        if (autoBackup) {
            jmsTemplate.convertAndSend(queueName + MQConst.ActiveMQ.BACKUP_QUEUE_SUFFIX, message, messagePostProcessor);
        }
    }

}
