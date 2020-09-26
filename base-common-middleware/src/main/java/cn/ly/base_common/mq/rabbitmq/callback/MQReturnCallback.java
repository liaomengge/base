package cn.ly.base_common.mq.rabbitmq.callback;

import cn.ly.base_common.utils.json.LyJsonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.google.common.collect.Maps;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by liaomengge on 16/12/19.
 */
public class MQReturnCallback implements RabbitTemplate.ReturnCallback {

    private static final Logger log = LyLogger.getInstance(MQReturnCallback.class);

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
