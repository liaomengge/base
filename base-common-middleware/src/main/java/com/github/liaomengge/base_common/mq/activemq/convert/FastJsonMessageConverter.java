package com.github.liaomengge.base_common.mq.activemq.convert;

import com.github.liaomengge.base_common.mq.consts.MQConst;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;
import java.io.UnsupportedEncodingException;

/**
 * Created by liaomengge on 2018/12/4.
 */
public class FastJsonMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        if (object instanceof String) {
            return session.createTextMessage((String) object);
        }
        if (object instanceof Message) {
            return (Message) object;
        }
        return session.createTextMessage(LyJsonUtil.toJson(object));
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        if (message instanceof TextMessage) {
            return LyJsonUtil.fromJson(((TextMessage) message).getText());
        }
        if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            try {
                String body = new String(bytes, MQConst.DEFAULT_CHARSET);
                return LyJsonUtil.fromJson(body);
            } catch (UnsupportedEncodingException e) {
                throw new MessageConversionException("Cannot convert bytes to String", e);
            }
        }
        throw new IllegalArgumentException("Unsupported message type [" + message.getClass() + "], " +
                "FastJsonMessageConverter by default only supports TextMessages and BytesMessages.");
    }
}
