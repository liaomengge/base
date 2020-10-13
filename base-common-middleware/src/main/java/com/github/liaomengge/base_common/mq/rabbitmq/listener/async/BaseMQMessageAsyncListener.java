package com.github.liaomengge.base_common.mq.rabbitmq.listener.async;

import com.github.liaomengge.base_common.mq.consts.MetricsConst;
import com.github.liaomengge.base_common.mq.domain.MQMessage;
import com.github.liaomengge.base_common.mq.domain.MessageHeader;
import com.github.liaomengge.base_common.mq.rabbitmq.AbstractMQMessageListener;
import com.github.liaomengge.base_common.mq.rabbitmq.domain.QueueConfig;
import com.github.liaomengge.base_common.mq.rabbitmq.monitor.DefaultMQMonitor;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.github.liaomengge.base_common.utils.thread.LyThreadPoolExecutorUtil;
import com.github.liaomengge.base_common.utils.trace.LyTraceLogUtil;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class BaseMQMessageAsyncListener<T extends MQMessage> extends AbstractMQMessageListener<T> implements InitializingBean {

    @Getter
    protected QueueConfig queueConfig;
    protected DefaultMQMonitor mqMonitor;

    @Setter
    private Executor bizTaskExecutor;

    public BaseMQMessageAsyncListener(QueueConfig queueConfig, DefaultMQMonitor mqMonitor) {
        this.queueConfig = queueConfig;
        this.mqMonitor = mqMonitor;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        bizTaskExecutor.execute(() -> {
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
                mqMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + queueConfig.getExchangeName(),
                        endTime - startTime);
                LyTraceLogUtil.clearTrace();
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(bizTaskExecutor)) {
            bizTaskExecutor = LyThreadPoolExecutorUtil.buildSimpleThreadPool("async-listener",
                    new LinkedBlockingQueue<>(16));
        }
    }
}
