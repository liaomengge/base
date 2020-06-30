package cn.ly.base_common.mq.rabbitmq.listener;

import cn.ly.base_common.helper.metric.rabbitmq.RabbitMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class SimpleMQMessageListener<T extends MQMessage> extends BaseMQMessageListener<T> {

    public SimpleMQMessageListener(QueueConfig queueConfig, RabbitMQMonitor rabbitMQMonitor) {
        super(queueConfig, rabbitMQMonitor);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            rabbitMQMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + queueConfig.getExchangeName());
            logger.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
            throw new AmqpRejectAndDontRequeueException("Ack Exception", e);
        }

        super.onMessage(message, channel);
    }
}
