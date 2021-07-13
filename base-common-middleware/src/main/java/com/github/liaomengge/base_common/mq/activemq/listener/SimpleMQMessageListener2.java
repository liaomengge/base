package com.github.liaomengge.base_common.mq.activemq.listener;


import com.github.liaomengge.base_common.mq.activemq.domain.QueueConfig;
import com.github.liaomengge.base_common.mq.activemq.monitor.DefaultMQMonitor;
import com.github.liaomengge.base_common.mq.consts.MetricsConst;
import com.github.liaomengge.base_common.mq.domain.MQMessage;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class SimpleMQMessageListener2<T extends MQMessage> extends BaseMQMessageListener<T> {

    public SimpleMQMessageListener2(QueueConfig queueConfig, DefaultMQMonitor mqMonitor) {
        super(queueConfig, mqMonitor);
    }

    @Override
    public void onMessage(Message message) {
        try {
            super.onMessage(message);
        } finally {
            try {
                message.acknowledge();
            } catch (JMSException e) {
                mqMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
                log.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
            }
        }
    }
}
