package cn.mwee.base_common.mq.rabbitmq.callback;

import cn.mwee.base_common.helper.mail.MailHelper;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.net.MwNetworkUtil;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * Created by liaomengge on 16/12/19.
 */
public class MQConfirmCallback implements RabbitTemplate.ConfirmCallback {

    private static final Logger logger = MwLogger.getInstance(MQConfirmCallback.class);

    private MailHelper mailHelper;

    public MQConfirmCallback(MailHelper mailHelper) {
        this.mailHelper = mailHelper;
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //可能存在恶意消息投递攻击
        if (!ack) {
            logger.error("send message to exchange failed, cause ===> {}", cause);
            mailHelper.sendTextMail(MwNetworkUtil.getHostAddress() + "/" + MwNetworkUtil.getHostName() +
                    "-[RabbitMQ Confirm Callback]失败!", cause);
        }
    }
}
