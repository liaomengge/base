package cn.ly.base_common.mq.activemq;

import cn.ly.base_common.mq.consts.MQConst;
import cn.ly.base_common.mq.domain.MQMessage;
import cn.ly.base_common.mq.domain.MessageHeader;
import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Objects;

/**
 * Created by liaomengge on 16/12/22.
 */
public abstract class AbstractMQMessageListener<T extends MQMessage> implements MessageListener {

    protected static final Logger log = LyLogger.getInstance(AbstractMQMessageListener.class);

    protected T parseMessage(Message message) {
        String receiveMsg = this.getText(message);
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
        try {
            String traceId = message.getStringProperty(MQConst.MQ_TRACE_ID);
            long sendTime = message.getLongProperty(MQConst.MQ_SEND_TIME);
            return new MessageHeader(traceId, sendTime);
        } catch (JMSException e) {
            return new MessageHeader();
        }
    }

    protected abstract void processListener(T t);

    protected Class<T> getEntityClass() {
        return null;
    }

    protected TypeReference<T> getTypeReference() {
        return null;
    }

    private String getText(Message message) {
        if (message instanceof TextMessage) {
            try {
                return ((TextMessage) message).getText();
            } catch (JMSException e) {
                return message.toString();
            }
        }
        return message.toString();
    }
}
