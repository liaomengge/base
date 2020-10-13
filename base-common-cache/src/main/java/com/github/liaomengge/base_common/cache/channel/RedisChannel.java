package com.github.liaomengge.base_common.cache.channel;

import com.github.liaomengge.base_common.cache.redis.RedissonClientManager;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

/**
 * Created by liaomengge on 2019/3/20.
 */
@AllArgsConstructor
public class RedisChannel implements Channel {

    private static final String TOPIC_PREFIX = "redis:topic:channel:";

    private final String channelName;
    private final RedissonClientManager redissonClientManager;

    @Override
    public void doPubChannel(String msg) {
        String topicKey = this.getTopicKey();
        this.redissonClientManager.getRedissonClient().getTopic(topicKey).publishAsync(msg);
    }

    @Override
    public void doSubChannel(Consumer<String> consumer) {
        String topicKey = this.getTopicKey();
        this.redissonClientManager.getRedissonClient().getTopic(topicKey).addListenerAsync(String.class,
                (channel, msg) -> consumer.accept(msg));
    }

    @Override
    public String getChannelName() {
        return this.channelName;
    }

    private String getTopicKey() {
        return TOPIC_PREFIX + this.getChannelName();
    }
}
