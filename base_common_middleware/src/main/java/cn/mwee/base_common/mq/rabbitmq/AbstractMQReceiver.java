package cn.mwee.base_common.mq.rabbitmq;

import cn.mwee.base_common.utils.log4j2.MwLogger;
import cn.mwee.base_common.utils.shutdown.MwShutdownUtil;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.Executor;

/**
 * Created by liaomengge on 17/9/15.
 */
public abstract class AbstractMQReceiver implements DisposableBean {

    protected static final Logger logger = MwLogger.getInstance(AbstractMQReceiver.class);

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
        MwShutdownUtil.registerShutdownHook(() -> {
            try {
                logger.info("RabbitMQ Listener Exist...");
            } finally {
                if (listenerContainer != null) {
                    listenerContainer.shutdown();
                }
            }
        });
    }
}
