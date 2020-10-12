package cn.ly.base_common.mq.rabbitmq.listener;

import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.mq.rabbitmq.AbstractMQMessageListener;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import cn.ly.base_common.mq.rabbitmq.monitor.DefaultMQMonitor;
import cn.ly.base_common.utils.date.LyJdk8DateUtil;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.trace.LyTraceLogUtil;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import org.springframework.amqp.core.Message;

/**
 * Created by liaomengge on 16/12/22.
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
    public void onMessage(Message message, Channel channel) throws Exception {
        long startTime = LyJdk8DateUtil.getMilliSecondsTime();
        long endTime;
        T t = null;
        try {
            t = parseMessage(message);
            if (t == null) {
                return;
            }
            MessageHeader messageHeader = resolveMessageHeader(message);
            mqMonitor.monitorTime(MetricsConst.SEND_2_RECEIVE_EXEC_TIME + "." + queueConfig.getExchangeName(),
                    LyJdk8DateUtil.getMilliSecondsTime() - messageHeader.getSendTime());

            LyTraceLogUtil.put(messageHeader.getMqTraceId());
            startTime = LyJdk8DateUtil.getMilliSecondsTime();
            //业务逻辑
            processListener(t);

            mqMonitor.monitorCount(MetricsConst.DEQUEUE_COUNT + "." + queueConfig.getExchangeName());
        } catch (Exception e) {
            mqMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + queueConfig.getExchangeName());
            log.error("Handle Message[" + LyJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
        } finally {
            endTime = LyJdk8DateUtil.getMilliSecondsTime();
            mqMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + queueConfig.getExchangeName()
                    , endTime - startTime);
            LyTraceLogUtil.clearTrace();
        }
    }
}
