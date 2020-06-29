package cn.mwee.base_common.mq.rabbitmq.receiver;

import cn.mwee.base_common.mq.rabbitmq.listener.async.BaseMQMessageAsyncListener;
import lombok.NonNull;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

/**
 * Created by liaomengge on 2018/6/29.
 */
public class SimpleMQAsyncReceiver extends BaseMQReceiver {

    @Setter
    @NonNull
    private BaseMQMessageAsyncListener baseMQMessageAsyncListener;

    public SimpleMQAsyncReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                                 int maxConcurrentConsumers, int prefetchCount) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount);
    }

    public SimpleMQAsyncReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                                 int maxConcurrentConsumers, int prefetchCount, long receiveTimeout) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, receiveTimeout);
    }

    public SimpleMQAsyncReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                                 int maxConcurrentConsumers, int prefetchCount, long receiveTimeout,
                                 Advice[] adviceChain) {
        super(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, receiveTimeout,
                adviceChain);
    }

    @Override
    protected ChannelAwareMessageListener buildMessageListener() {
        return this.baseMQMessageAsyncListener;
    }

    @Override
    protected String[] buildQueueNames() {
        return this.baseMQMessageAsyncListener.getQueueConfig().buildQueueNames();
    }

}
