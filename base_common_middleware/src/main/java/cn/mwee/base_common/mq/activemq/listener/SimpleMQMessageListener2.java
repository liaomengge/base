package cn.mwee.base_common.mq.activemq.listener;


import cn.mwee.base_common.helper.metric.activemq.ActiveMQMonitor;
import cn.mwee.base_common.mq.activemq.domain.QueueConfig;
import cn.mwee.base_common.mq.consts.MetricsConst;
import cn.mwee.base_common.mq.domain.MQMessage;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class SimpleMQMessageListener2<T extends MQMessage> extends BaseMQMessageListener<T> {

    public SimpleMQMessageListener2(QueueConfig queueConfig, ActiveMQMonitor activeMQMonitor) {
        super(queueConfig, activeMQMonitor);
    }

    @Override
    public void onMessage(Message message) {
        try {
            super.onMessage(message);
        } finally {
            try {
                message.acknowledge();
            } catch (JMSException e) {
                activeMQMonitor.monitorCount(MetricsConst.EXEC_ACK_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
                logger.error("Enq Message[" + message.toString() + "], Ack Exception ===> ", e);
            }
        }
    }
}
