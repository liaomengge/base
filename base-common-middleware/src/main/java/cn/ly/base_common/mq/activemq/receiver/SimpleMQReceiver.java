package cn.ly.base_common.mq.activemq.receiver;

import cn.ly.base_common.mq.activemq.listener.BaseMQMessageListener;

import javax.jms.MessageListener;

import org.apache.activemq.pool.PooledConnectionFactory;

import lombok.NonNull;
import lombok.Setter;

/**
 * Created by liaomengge on 17/9/15.
 */
public class SimpleMQReceiver extends BaseMQReceiver {

    @Setter
    @NonNull
    private BaseMQMessageListener baseMQMessageListener;

    public SimpleMQReceiver(PooledConnectionFactory connectionFactory, String concurrency) {
        super(connectionFactory, concurrency);
    }

    @Override
    protected MessageListener buildMessageListener() {
        return this.baseMQMessageListener;
    }

    @Override
    protected String[] buildQueueNames() {
        return this.baseMQMessageListener.getQueueConfig().buildQueueNames();
    }
}