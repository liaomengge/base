package com.github.liaomengge.base_common.helper.redis.pubsub;

import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.error.LyThrowableUtil;
import com.github.liaomengge.base_common.utils.net.LyNetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * Created by liaomengge on 17/9/7.
 */
@Slf4j
public class RedisMessagePublisher implements MessagePublisher {
    
    private final StringRedisTemplate stringRedisTemplate;

    private final ChannelTopic channelTopic;

    private final MailHelper mailHelper;

    public RedisMessagePublisher(StringRedisTemplate stringRedisTemplate, ChannelTopic channelTopic,
                                 MailHelper mailHelper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.channelTopic = channelTopic;
        this.mailHelper = mailHelper;
    }

    @Override
    public void publish(String message) {
        try {
            stringRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
        } catch (Exception e) {
            log.error("Publish Redis异常 ===> ", e);
            if (mailHelper != null) {
                mailHelper.sendTextMail(LyNetworkUtil.getIpAddress() + "/" + LyNetworkUtil.getHostName() +
                        "发布消息异常!", LyJdk8DateUtil.getNowDate2String() + " ===> " + LyThrowableUtil.getStackTrace(e));
            }
        }
    }
}
