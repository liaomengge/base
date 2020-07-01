package cn.ly.base_common.mq.activemq.listener;


import cn.ly.base_common.helper.metric.activemq.ActiveMQMonitor;
import cn.ly.base_common.mq.activemq.AbstractMQMessageListener;
import cn.ly.base_common.mq.activemq.domain.QueueConfig;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
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
        long startTime = LyJdk8DateUtil.getMilliSecondsTime();
        long endTime;
        T t = null;
        try {
            t = super.parseMessage(message);
            if (t == null) {
                return;
            }
            MessageHeader messageHeader = resolveMessageHeader(message);
            activeMQMonitor.monitorTime(MetricsConst.SEND_2_RECEIVE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    LyJdk8DateUtil.getMilliSecondsTime() - messageHeader.getSendTime());

            LyTraceLogUtil.put(messageHeader.getMqTraceId());
            startTime = LyJdk8DateUtil.getMilliSecondsTime();
            //业务逻辑
            processListener(t);

            activeMQMonitor.monitorCount(MetricsConst.DEQUEUE_COUNT + "." + this.queueConfig.getBaseQueueName());
        } catch (Exception e) {
            activeMQMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
            logger.error("Handle Message[" + LyJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
        } finally {
            endTime = LyJdk8DateUtil.getMilliSecondsTime();
            activeMQMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    endTime - startTime);
            LyTraceLogUtil.clearTrace();
        }
    }
}
