package com.github.liaomengge.base_common.mq.rabbitmq.convert;

import com.alibaba.fastjson.TypeReference;
import com.github.liaomengge.base_common.mq.consts.MQConst;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Created by liaomengge on 16/12/20.
 */
public class FastJsonMessageConverter extends AbstractMessageConverter {

    @Override
    protected Message createMessage(Object obj, MessageProperties messageProperties) {
        byte[] bytes;
        try {
            String jsonString = LyJsonUtil.toJson(obj);
            bytes = jsonString.getBytes(MQConst.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException("Failed to convert Message content", e);
        }
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding(MQConst.DEFAULT_CHARSET);
        if (bytes != null) {
            messageProperties.setContentLength(bytes.length);
        }
        return new Message(bytes, messageProperties);

    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        byte[] body = message.getBody();
        if (Objects.isNull(body)) {
            return null;
        }
        String json;
        try {
            json = new String(body, MQConst.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException("Failed to convert Message content", e);
        }

        return LyJsonUtil.fromJson(json);
    }

    public <T> T fromMessage(Message message, T t) {
        byte[] body = message.getBody();
        if (Objects.isNull(body)) {
            return null;
        }
        String json;
        try {
            json = new String(body, MQConst.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException("Failed to convert Message content", e);
        }

        return LyJsonUtil.fromJson(json, new TypeReference<T>() {
        });
    }
}
