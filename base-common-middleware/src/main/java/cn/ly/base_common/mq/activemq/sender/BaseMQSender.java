package cn.ly.base_common.mq.activemq.sender;

import cn.ly.base_common.helper.metric.activemq.ActiveMQMonitor;
import cn.ly.base_common.mq.activemq.AbstractMQSender;

import java.util.Objects;

import javax.jms.DeliveryMode;
import javax.jms.Session;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import lombok.Setter;

/**
 * Created by liaomengge on 2018/12/7.
 */
public abstract class BaseMQSender extends AbstractMQSender {

    protected PooledConnectionFactory connectionFactory;
    protected MessageConverter messageConverter;
    protected JmsTemplate jmsTemplate;
    protected ActiveMQMonitor activeMQMonitor;

    @Setter
    protected boolean autoBackup = false;//是否自动将消息备份到_B的队列, 方便调试

    public BaseMQSender(PooledConnectionFactory connectionFactory, ActiveMQMonitor activeMQMonitor) {
        this(connectionFactory, new MappingJackson2MessageConverter(), activeMQMonitor);
    }

    public BaseMQSender(PooledConnectionFactory connectionFactory, MessageConverter messageConverter,
                        ActiveMQMonitor activeMQMonitor) {
        this.connectionFactory = connectionFactory;
        this.messageConverter = messageConverter;
        this.activeMQMonitor = activeMQMonitor;
        jmsTemplate = new JmsTemplate(this.connectionFactory);
        if (Objects.nonNull(this.messageConverter)) {
            jmsTemplate.setMessageConverter(this.messageConverter);
        }
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    }
}
