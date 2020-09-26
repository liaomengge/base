package cn.ly.base_common.mq.rabbitmq.callback;

import cn.ly.base_common.helper.mail.MailHelper;
import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.net.LyNetworkUtil;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by liaomengge on 16/12/19.
 */
public class MQConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private static final Logger log = LyLogger.getInstance(MQConfirmCallback.class);

    private MailHelper mailHelper;

    public MQConfirmCallback(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //可能存在恶意消息投递攻击
        if (!ack) {
            log.error("send message to exchange failed, cause ===> {}", cause);
            mailHelper.sendTextMail(LyNetworkUtil.getHostAddress() + "/" + LyNetworkUtil.getHostName() +
                    "-[RabbitMQ Confirm Callback]失败!", cause);
        }
    }
}
