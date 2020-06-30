package cn.ly.base_common.helper.redis.pubsub;

/**
 * Created by liaomengge on 17/9/7.
 */
public interface MessagePublisher {
    void publish(String message);
}
