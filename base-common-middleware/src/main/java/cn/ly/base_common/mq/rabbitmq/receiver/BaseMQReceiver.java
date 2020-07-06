package cn.ly.base_common.mq.rabbitmq.receiver;

import cn.ly.base_common.mq.rabbitmq.AbstractMQReceiver;
import cn.ly.base_common.utils.thread.LyThreadPoolExecutorUtil;
import org.aopalliance.aop.Advice;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static cn.ly.base_common.mq.consts.MQConst.DEFAULT_RECEIVE_TIMEOUT;

/**
 * Created by liaomengge on 2018/12/6.
 */
public abstract class BaseMQReceiver extends AbstractMQReceiver implements InitializingBean {

    private CachingConnectionFactory connectionFactory;
    private int concurrentConsumers;
    private int maxConcurrentConsumers;
    private int prefetchCount;
    private long receiveTimeout;//接收超时时间
    private Advice[] adviceChain;
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    public BaseMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                          int maxConcurrentConsumers, int prefetchCount) {
        this(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, DEFAULT_RECEIVE_TIMEOUT);
    }

    public BaseMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                          int maxConcurrentConsumers, int prefetchCount, long receiveTimeout) {
        this(connectionFactory, concurrentConsumers, maxConcurrentConsumers, prefetchCount, receiveTimeout, null);
    }

    public BaseMQReceiver(CachingConnectionFactory connectionFactory, int concurrentConsumers,
                          int maxConcurrentConsumers, int prefetchCount, long receiveTimeout, Advice[] adviceChain) {
        this.connectionFactory = connectionFactory;
        this.concurrentConsumers = concurrentConsumers;
        this.maxConcurrentConsumers = maxConcurrentConsumers;
        this.prefetchCount = prefetchCount;
        this.receiveTimeout = receiveTimeout;
        this.adviceChain = adviceChain;
        simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(this.connectionFactory);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleMessageListenerContainer.setConcurrentConsumers(this.concurrentConsumers);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(this.maxConcurrentConsumers);
        simpleMessageListenerContainer.setPrefetchCount(this.prefetchCount);
        simpleMessageListenerContainer.setReceiveTimeout(this.receiveTimeout);
        if (ArrayUtils.isNotEmpty(this.adviceChain)) {
            simpleMessageListenerContainer.setAdviceChain(this.adviceChain);
        }
    }

    protected abstract ChannelAwareMessageListener buildMessageListener();

    protected abstract String[] buildQueueNames();

    @Override
    public void start() {
        simpleMessageListenerContainer.initialize();
        simpleMessageListenerContainer.start();
    }

    @Override
    public void stop() {
        if (simpleMessageListenerContainer.isRunning()) {
            simpleMessageListenerContainer.stop(() -> logger.info("队列[{}]监听器已经停止...",
                    Arrays.toString(simpleMessageListenerContainer.getQueueNames())));
        }
    }

    @Override
    public void destroy() {
        simpleMessageListenerContainer.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        simpleMessageListenerContainer.setMessageListener(this.buildMessageListener());
        simpleMessageListenerContainer.setQueueNames(this.buildQueueNames());
        if (consumerExecutor != null) {
            simpleMessageListenerContainer.setTaskExecutor(consumerExecutor);
        } else {
            consumerExecutor = LyThreadPoolExecutorUtil.buildThreadPool(maxConcurrentConsumers,
                    concurrentConsumers + maxConcurrentConsumers, "rabbitmq-consumer",
                    30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(16));
            simpleMessageListenerContainer.setTaskExecutor(consumerExecutor);
        }
        super.registerShutdownHook(simpleMessageListenerContainer);
    }
}
