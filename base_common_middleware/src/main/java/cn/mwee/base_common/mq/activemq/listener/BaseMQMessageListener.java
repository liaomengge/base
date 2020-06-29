package cn.mwee.base_common.mq.activemq.listener;


import cn.mwee.base_common.helper.metric.activemq.ActiveMQMonitor;
import cn.mwee.base_common.mq.activemq.AbstractMQMessageListener;
import cn.mwee.base_common.mq.activemq.domain.QueueConfig;
import cn.mwee.base_common.mq.consts.MetricsConst;
import cn.mwee.base_common.mq.domain.MQMessage;
import cn.mwee.base_common.mq.domain.MessageHeader;
import cn.mwee.base_common.utils.date.MwJdk8DateUtil;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import cn.mwee.base_common.utils.trace.MwTraceLogUtil;
import lombok.Getter;

import javax.jms.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class BaseMQMessageListener<T extends MQMessage> extends AbstractMQMessageListener<T> {

    @Getter
    protected QueueConfig queueConfig;
    protected ActiveMQMonitor activeMQMonitor;

    public BaseMQMessageListener(QueueConfig queueConfig, ActiveMQMonitor activeMQMonitor) {
        this.queueConfig = queueConfig;
        this.activeMQMonitor = activeMQMonitor;
    }

    @Override
    public void onMessage(Message message) {
        long startTime = MwJdk8DateUtil.getMilliSecondsTime();
        long endTime;
        T t = null;
        try {
            t = super.parseMessage(message);
            if (t == null) {
                return;
            }
            MessageHeader messageHeader = resolveMessageHeader(message);
            activeMQMonitor.monitorTime(MetricsConst.SEND_2_RECEIVE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    MwJdk8DateUtil.getMilliSecondsTime() - messageHeader.getSendTime());

            MwTraceLogUtil.put(messageHeader.getMqTraceId());
            startTime = MwJdk8DateUtil.getMilliSecondsTime();
            //业务逻辑
            processListener(t);

            activeMQMonitor.monitorCount(MetricsConst.DEQUEUE_COUNT + "." + this.queueConfig.getBaseQueueName());
        } catch (Exception e) {
            activeMQMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
            logger.error("Handle Message[" + MwJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
        } finally {
            endTime = MwJdk8DateUtil.getMilliSecondsTime();
            activeMQMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    endTime - startTime);
            MwTraceLogUtil.clearTrace();
        }
    }
}
