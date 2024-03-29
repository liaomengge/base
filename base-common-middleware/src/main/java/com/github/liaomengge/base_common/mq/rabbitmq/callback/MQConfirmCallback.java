package com.github.liaomengge.base_common.mq.rabbitmq.callback;

import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by liaomengge on 16/12/19.
 */
@Slf4j
public class MQConfirmCallback implements RabbitTemplate.ConfirmCallback {
    
    private MailHelper mailHelper;

    public MQConfirmCallback(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //可能存在恶意消息投递攻击
        if (!ack) {
            log.error("send message to exchange failed, cause ===> {}", cause);
            mailHelper.sendTextMail(LyNetworkUtil.getIpAddress() + "/" + LyNetworkUtil.getHostName() +
                    "-[RabbitMQ Confirm Callback]失败!", cause);
        }
    }
}
