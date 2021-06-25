package com.github.liaomengge.base_common.mq.activemq.listener;


import com.github.liaomengge.base_common.mq.activemq.AbstractMQMessageListener;
import com.github.liaomengge.base_common.mq.activemq.domain.QueueConfig;
import com.github.liaomengge.base_common.mq.activemq.monitor.DefaultMQMonitor;
import com.github.liaomengge.base_common.mq.consts.MetricsConst;
import com.github.liaomengge.base_common.mq.domain.MQMessage;
import com.github.liaomengge.base_common.mq.domain.MessageHeader;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import lombok.Getter;

import javax.jms.Message;

/**
 * Created by liaomengge on 17/1/3.
 */
public abstract class BaseMQMessageListener<T extends MQMessage> extends AbstractMQMessageListener<T> {

    @Getter
    protected QueueConfig queueConfig;
    protected DefaultMQMonitor mqMonitor;

    public BaseMQMessageListener(QueueConfig queueConfig, DefaultMQMonitor mqMonitor) {
        this.queueConfig = queueConfig;
        this.mqMonitor = mqMonitor;
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
            mqMonitor.monitorTime(MetricsConst.SEND_2_RECEIVE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    LyJdk8DateUtil.getMilliSecondsTime() - messageHeader.getSendTime());

            LyTraceLogUtil.put(messageHeader.getMqTraceId());
            startTime = LyJdk8DateUtil.getMilliSecondsTime();
            //业务逻辑
            processListener(t);

            mqMonitor.monitorCount(MetricsConst.DEQUEUE_COUNT + "." + this.queueConfig.getBaseQueueName());
        } catch (Exception e) {
            mqMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + this.queueConfig.getBaseQueueName());
            log.error("Handle Message[" + LyJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
        } finally {
            endTime = LyJdk8DateUtil.getMilliSecondsTime();
            mqMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + this.queueConfig.getBaseQueueName(),
                    endTime - startTime);
            LyTraceLogUtil.clear();
        }
    }
}
