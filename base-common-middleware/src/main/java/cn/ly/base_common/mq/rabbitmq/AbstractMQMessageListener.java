package cn.ly.base_common.mq.rabbitmq;

import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class AbstractMQMessageListener<T extends MQMessage> implements ChannelAwareMessageListener {

    protected static final Logger log = LyLogger.getInstance(AbstractMQMessageListener.class);

    protected T parseMessage(Message message) {
        String receiveMsg = new String(message.getBody());
        log.info("receive message: {}", receiveMsg);

        if (StringUtils.isBlank(receiveMsg)) {
            return null;
        }

        Class<T> clz = this.getEntityClass();
        if (Objects.nonNull(clz)) {
            return LyJacksonUtil.fromJson(receiveMsg, clz);
        }
        TypeReference<T> typeReference = this.getTypeReference();
        if (Objects.nonNull(typeReference)) {
            return LyJacksonUtil.fromJson(receiveMsg, typeReference);
        }
        return (T) LyJacksonUtil.fromJson(receiveMsg);
    }

    protected MessageHeader resolveMessageHeader(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        String traceId = MapUtils.getString(messageProperties.getHeaders(), MQConst.MQ_TRACE_ID);
        long sendTime = MapUtils.getLongValue(messageProperties.getHeaders(), MQConst.MQ_SEND_TIME);
        return new MessageHeader(traceId, sendTime);
    }

    protected abstract void processListener(T t);

    protected Class<T> getEntityClass() {
        return null;
    }

    protected TypeReference<T> getTypeReference() {
        return null;
    }
}
