package cn.ly.base_common.mq.activemq.receiver;

import cn.ly.base_common.mq.activemq.AbstractMQReceiver;
import cn.ly.base_common.mq.activemq.exception.DefaultErrorHandler;
import cn.ly.base_common.mq.activemq.exception.DefaultExceptionListener;
import com.google.common.collect.Lists;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.annotation.PostConstruct;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liaomengge on 2018/12/7.
 */
public abstract class BaseMQReceiver extends AbstractMQReceiver {

    private PooledConnectionFactory connectionFactory;
    private String concurrency;//最大线程数
    private List<SimpleMessageListenerContainer> simpleMessageListenerContainers;

    public BaseMQReceiver(PooledConnectionFactory connectionFactory, String concurrency) {
        this.connectionFactory = connectionFactory;
        this.concurrency = concurrency;
    }

    protected abstract MessageListener buildMessageListener();

    protected abstract String[] buildQueueNames();

    @Override
    public void start() {
        simpleMessageListenerContainers.forEach(simpleMessageListenerContainer -> {
            simpleMessageListenerContainer.initialize();
            if (!simpleMessageListenerContainer.isRunning()) {
                simpleMessageListenerContainer.start();
            }
        });
    }

    @Override
    public void stop() {
        simpleMessageListenerContainers.forEach(simpleMessageListenerContainer -> {
            if (simpleMessageListenerContainer.isRunning()) {
                simpleMessageListenerContainer.destroy();
                logger.info("队列[{}]监听器已经停止...", simpleMessageListenerContainer.getDestinationName());
            }
        });
    }

    @PostConstruct
    private void init() {
        String[] queueNames = this.buildQueueNames();
        simpleMessageListenerContainers = Lists.newArrayListWithCapacity(queueNames.length);
        Arrays.stream(queueNames).forEach(queueName -> {
            SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
            simpleMessageListenerContainer.setConnectionFactory(this.connectionFactory);
            simpleMessageListenerContainer.setMessageListener(this.buildMessageListener());
            simpleMessageListenerContainer.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
            simpleMessageListenerContainer.setSessionTransacted(false);
            simpleMessageListenerContainer.setDestinationName(queueName);
            simpleMessageListenerContainer.setConcurrency(this.concurrency);
            simpleMessageListenerContainer.setErrorHandler(new DefaultErrorHandler());
            simpleMessageListenerContainer.setExceptionListener(new DefaultExceptionListener());

            if (asyncExec && bizTaskExecutor != null) {
                simpleMessageListenerContainer.setTaskExecutor(bizTaskExecutor);
            }
            simpleMessageListenerContainers.add(simpleMessageListenerContainer);
        });
        super.registerShutdownHook(simpleMessageListenerContainers.stream().toArray(SimpleMessageListenerContainer[]::new));
    }

    @Override
    public void destroy() {
        simpleMessageListenerContainers.forEach(simpleMessageListenerContainer -> simpleMessageListenerContainer.destroy());
    }
}
