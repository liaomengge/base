package cn.ly.base_common.mq.rabbitmq.listener;

import cn.ly.base_common.helper.metric.rabbitmq.RabbitMQMonitor;
import cn.ly.base_common.mq.consts.MetricsConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.mq.rabbitmq.domain.QueueConfig;
import cn.ly.base_common.mq.rabbitmq.enums.DeadLetterReasonEnum;
import cn.ly.base_common.utils.date.MwJdk8DateUtil;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.trace.MwTraceLogUtil;
import com.alibaba.fastjson.JSONException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.ConsumerCancelledException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class RetryMQMessageListener<T extends MQMessage> extends BaseMQMessageListener<T> {

    @Setter
    private int retryPublish = 3;

    public RetryMQMessageListener(QueueConfig queueConfig, RabbitMQMonitor rabbitMQMonitor) {
        super(queueConfig, rabbitMQMonitor);
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
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
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (ClassCastException | JSONException e) {
            logger.error("Receive Message[" + MwJsonUtil.toJson4Log(t) + "] Format Error ===> ", e);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (ShutdownSignalException | ConsumerCancelledException e) {
            logger.error("Handle Message[" + MwJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
        } catch (Exception e) {
            rabbitMQMonitor.monitorCount(MetricsConst.EXEC_EXCEPTION + "." + queueConfig.getExchangeName());
            logger.error("Handle Message[" + MwJsonUtil.toJson4Log(t) + "] Failed ===> ", e);
            try {
                MessageProperties messageProperties = message.getMessageProperties();
                Map<String, Object> headerMap = messageProperties.getHeaders();
                if (MapUtils.isNotEmpty(headerMap)) {
                    List<Map<String, Object>> xDeathList = (List<Map<String, Object>>) headerMap.get("x-death");
                    if (CollectionUtils.isNotEmpty(xDeathList)) {
                        for (Map<String, Object> xDeath : xDeathList) {
                            if (DeadLetterReasonEnum.EXPIRED.getCode().equals(xDeath.get("reason")) &&
                                    MapUtils.getIntValue(xDeath, "count") >= retryPublish) {
                                channel.basicAck(messageProperties.getDeliveryTag(), false);
                                return;
                            }
                        }
                    }
                }

                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioe) {
                logger.error("Enq Message[" + message.toString() + "], Reject/Nack Exception ===> ", ioe);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } finally {
            endTime = MwJdk8DateUtil.getMilliSecondsTime();
            rabbitMQMonitor.monitorTime(MetricsConst.RECEIVE_2_HANDLE_EXEC_TIME + "." + queueConfig.getExchangeName(),
                    endTime - startTime);
            MwTraceLogUtil.clearTrace();
        }
    }
}
