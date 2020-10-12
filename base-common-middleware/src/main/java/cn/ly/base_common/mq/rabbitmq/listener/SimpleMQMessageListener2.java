package cn.ly.base_common.mq.rabbitmq.listener;

import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import cn.ly.base_common.mq.rabbitmq.monitor.DefaultMQMonitor;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class SimpleMQMessageListener2<T extends MQMessage> extends BaseMQMessageListener<T> {

    public SimpleMQMessageListener2(QueueConfig queueConfig, DefaultMQMonitor mqMonitor) {
        super(queueConfig, mqMonitor);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            super.onMessage(message, channel);
        } finally {
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                mqMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + queueConfig.getExchangeName());
                log.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
                throw new AmqpRejectAndDontRequeueException("Ack Exception", e);
            }
        }
    }
}
