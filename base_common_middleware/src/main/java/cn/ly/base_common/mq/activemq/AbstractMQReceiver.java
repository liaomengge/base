package cn.ly.base_common.mq.activemq;

import cn.ly.base_common.utils.log4j2.MwLogger;
import cn.ly.base_common.utils.shutdown.MwShutdownUtil;
import cn.ly.base_common.utils.thread.MwThreadPoolExecutorUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by liaomengge on 17/9/15.
 */
public abstract class AbstractMQReceiver implements DisposableBean {

    protected static final Logger logger = MwLogger.getInstance(AbstractMQReceiver.class);

    @Setter
    protected boolean asyncExec = false;//非异步执行

    @Getter
    @Setter
    protected ThreadPoolExecutor bizTaskExecutor = MwThreadPoolExecutorUtil.buildSimpleThreadPool("async-activemq",
            new LinkedBlockingQueue<>(16));

    public abstract void start();

    public abstract void stop();

    /**
     * 程序退出时的回调勾子
     *
     * @param simpleMessageListenerContainers
     */
    protected void registerShutdownHook(SimpleMessageListenerContainer... simpleMessageListenerContainers) {
        MwShutdownUtil.registerShutdownHook(() -> {
            try {
                logger.info("ActiveMQ Listener Exist...");
            } finally {
                if (simpleMessageListenerContainers != null && simpleMessageListenerContainers.length > 0) {
                    Arrays.stream(simpleMessageListenerContainers).forEach(listenerContainer -> listenerContainer.shutdown());
                }
            }
        });
    }
}
