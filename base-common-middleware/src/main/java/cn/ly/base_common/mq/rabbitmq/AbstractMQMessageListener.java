package cn.ly.base_common.mq.rabbitmq;

import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.Objects;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class AbstractMQMessageListener<T extends MQMessage> implements ChannelAwareMessageListener {

    protected static final Logger logger = LyLogger.getInstance(AbstractMQMessageListener.class);

    protected T parseMessage(Message message) {
        String receiveMsg = new String(message.getBody());
        logger.info("receive message: {}", receiveMsg);

        if (StringUtils.isBlank(receiveMsg)) {
            return null;
        }

        Class<T> clazz = this.getEntityClass();
        if (Objects.nonNull(clazz)) {
            return LyJsonUtil.fromJson(receiveMsg, clazz);
        }
        TypeReference<T> typeReference = this.getTypeReference();
        if (Objects.nonNull(typeReference)) {
            return LyJsonUtil.fromJson(receiveMsg, typeReference);
        }
        return (T) LyJsonUtil.fromJson(receiveMsg);
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
