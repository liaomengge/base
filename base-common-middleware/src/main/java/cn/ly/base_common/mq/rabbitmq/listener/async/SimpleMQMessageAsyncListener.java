package cn.ly.base_common.mq.rabbitmq.listener.async;

import cn.ly.base_common.helper.metric.rabbitmq.RabbitMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import com.rabbitmq.client.Channel;
import lombok.Setter;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;

import java.util.concurrent.Executor;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class SimpleMQMessageAsyncListener<T extends MQMessage> extends BaseMQMessageAsyncListener<T> {

    @Setter
    private Executor bizTaskExecutor;

    public SimpleMQMessageAsyncListener(QueueConfig queueConfig, RabbitMQMonitor rabbitMQMonitor) {
        super(queueConfig, rabbitMQMonitor);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            rabbitMQMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + queueConfig.getExchangeName());
            log.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
            throw new AmqpRejectAndDontRequeueException("Ack Exception", e);
        }

        super.onMessage(message, channel);
    }
}
