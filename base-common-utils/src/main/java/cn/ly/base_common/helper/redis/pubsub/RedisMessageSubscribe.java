package cn.ly.base_common.helper.redis.pubsub;

import cn.ly.base_common.utils.log4j2.LyLogger;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Created by liaomengge on 17/9/7.
 */
public abstract class RedisMessageSubscribe implements MessageListener {

    private static final Logger logger = LyLogger.getInstance(RedisMessageSubscribe.class);

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
                logger.warn("发布的消息[" + subscribeMessage + "]不合法!");
                return;
            }
            logger.info("发布的消息 ===> " + subscribeMessage);

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
