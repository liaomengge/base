package com.github.liaomengge.base_common.mq.rabbitmq.receiver;

import com.github.liaomengge.base_common.mq.rabbitmq.listener.BaseMQMessageListener;
import lombok.NonNull;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * Created by liaomengge on 2018/6/29.
 */
public class SimpleMQReceiver extends BaseMQReceiver {

    @Setter
    @NonNull
    private BaseMQMessageListener baseMQMessageListener;

    public SimpleMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                            int maxConcurrentConsumers, int prefetchCount) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount);
    }

    public SimpleMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                            int maxConcurrentConsumers, int prefetchCount, long receiveTimeout) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, receiveTimeout);
    }

    public SimpleMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                            int maxConcurrentConsumers, int prefetchCount, long receiveTimeout, Advice[] adviceChain) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, receiveTimeout,
                adviceChain);
    }

    @Override
    protected ChannelAwareMessageListener buildMessageListener() {
        return this.baseMQMessageListener;
    }

    @Override
    protected String[] buildQueueNames() {
        return this.baseMQMessageListener.getQueueConfig().buildQueueNames();
    }

}
