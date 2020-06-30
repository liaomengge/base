package cn.ly.base_common.mq.rabbitmq.listener.async;

import cn.ly.base_common.helper.metric.rabbitmq.RabbitMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.mq.rabbitmq.AbstractMQMessageListener;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import cn.ly.base_common.utils.date.MwJdk8DateUtil;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.thread.MwThreadPoolExecutorUtil;
import cn.ly.base_common.utils.trace.MwTraceLogUtil;
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
    protected RabbitMQMonitor rabbitMQMonitor;

    @Setter
    private Executor bizTaskExecutor;

    public BaseMQMessageAsyncListener(QueueConfig queueConfig, RabbitMQMonitor rabbitMQMonitor) {
        this.queueConfig = queueConfig;
        this.rabbitMQMonitor = rabbitMQMonitor;
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        bizTaskExecutor.execute(() -> {
            long startTime = MwJdk8DateUtil.getMilliSecondsTime();
            long endTime;
            T t = null;
            try {
                t = parseMessage(message);
                if (t == null) {
                    return;
                }
                MessageHeader messageHeader = resolveMessageHeader(message);
                rabbitMQMonitor.monitorTime(MetricsConst.SEND_2_RECEIVE_EXEC_TIME + "." + queueConfig.getExchangeName(),
                        MwJdk8DateUtil.getMilliSecondsTime() - messageHeader.getSendTime());

                MwTraceLogUtil.put(messageHeader.getMqTraceId());
                startTime = MwJdk8DateUtil.getMilliSecondsTime();
                //业务逻辑
                processListener(t);

                rabbitMQMonitor.monitorCount(MetricsConst.DEQUEUE_COUNT + "." + queueConfig.getExchangeName());
            } catch (Exception e) {
                rabbitMQMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + queueConfig.getExchangeName());
                logger.error("Handle Message[" + MwJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
            } finally {
                endTime = MwJdk8DateUtil.getMilliSecondsTime();
                rabbitMQMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + queueConfig.getExchangeName(),
                        endTime - startTime);
                MwTraceLogUtil.clearTrace();
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(bizTaskExecutor)) {
            bizTaskExecutor = MwThreadPoolExecutorUtil.buildSimpleThreadPool("async-listener",
                    new LinkedBlockingQueue<>(16));
        }
    }
}
