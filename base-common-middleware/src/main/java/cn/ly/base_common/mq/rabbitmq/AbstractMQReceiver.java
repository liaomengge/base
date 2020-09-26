package cn.ly.base_common.mq.rabbitmq;

import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.shutdown.LyShutdownUtil;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;

import lombok.Setter;

/**
 * Created by liaomengge on 17/9/15.
 */
public abstract class AbstractMQReceiver implements DisposableBean {

    protected static final Logger log = LyLogger.getInstance(AbstractMQReceiver.class);

    public abstract void start();

    public abstract void stop();

    //注：该核心线程coreThread,必须大于等于concurrentConsumers;否则,会await,见：SimpleMessageListenerContainer.doStart
    @Setter
    protected Executor consumerExecutor;

    /**
     * 程序退出时的回调勾子
     *
     * @param listenerContainer
     */
    protected void registerShutdownHook(AbstractMessageListenerContainer listenerContainer) {
        LyShutdownUtil.registerShutdownHook(() -> {
            try {
                log.info("RabbitMQ Listener Exist...");
            } finally {
                if (listenerContainer != null) {
                    listenerContainer.shutdown();
                }
            }
        });
    }
}
