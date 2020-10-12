package cn.ly.base_common.mq.activemq.listener;


import cn.ly.base_common.mq.activemq.domain.QueueConfig;
import cn.ly.base_common.mq.activemq.monitor.DefaultMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class SimpleMQMessageListener<T extends MQMessage> extends BaseMQMessageListener<T> {

    public SimpleMQMessageListener(QueueConfig queueConfig, DefaultMQMonitor mqMonitor) {
        super(queueConfig, mqMonitor);
    }

    @Override
    public void onMessage(Message message) {
        try {
            message.acknowledge();
        } catch (JMSException e) {
            mqMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
            log.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
        }

        super.onMessage(message);
    }
}
