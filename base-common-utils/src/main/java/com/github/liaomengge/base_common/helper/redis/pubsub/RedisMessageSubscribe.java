package com.github.liaomengge.base_common.helper.redis.pubsub;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Created by liaomengge on 17/9/7.
 */
@Slf4j
public abstract class RedisMessageSubscribe implements MessageListener {

    private static final String[] PUB_REG_EXP = {"*", "?", "[", "]", "\\"};

    @Getter
    @Setter
    private int maxLength = 180;

    @Getter
    @Setter
    private boolean isSubscribe = true;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        if (this.isSubscribe() && message != null) {
            String subscribeMessage = message.toString();
            if (StringUtils.isBlank(subscribeMessage) || containSpecialChar(subscribeMessage) || overMaxLength(subscribeMessage)) {
                log.warn("发布的消息[{}]不合法!", subscribeMessage);
                return;
            }
            log.info("发布的消息 ===> {}", subscribeMessage);

            this.doHandle(subscribeMessage);
        }
    }

    protected abstract void doHandle(String message);

    private boolean containSpecialChar(String str) {
        for (String regex : PUB_REG_EXP) {
            if (str.startsWith(regex) || str.endsWith(regex) || str.contains(regex)) {
                return true;
            }
        }
        return false;
    }

    private boolean overMaxLength(String str) {
        return str.length() > this.getMaxLength();
    }
}
