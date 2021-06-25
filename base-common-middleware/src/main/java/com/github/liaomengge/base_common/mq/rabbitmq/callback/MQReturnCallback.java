package com.github.liaomengge.base_common.mq.rabbitmq.callback;

import com.github.liaomengge.base_common.support.logger.JsonLogger;
import com.github.liaomengge.base_common.utils.json.LyJsonUtil;
import com.google.common.collect.Maps;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * Created by liaomengge on 16/12/19.
 */
public class MQReturnCallback implements RabbitTemplate.ReturnCallback {

    private static final JsonLogger log = JsonLogger.getInstance(MQReturnCallback.class);

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //记录投递exchange成功,后续处理失败的逻辑
        Map<String, Object> returnMap = Maps.newHashMap();
        returnMap.put("message", message.getMessageProperties());
        returnMap.put("replyCode", replyCode);
        returnMap.put("replyText", replyText);
        returnMap.put("exchange", exchange);
        returnMap.put("routingKey", routingKey);

        log.error("Return Callback Failed, Detail Message[{}]", LyJsonUtil.toJson4Log(returnMap));
    }
}
