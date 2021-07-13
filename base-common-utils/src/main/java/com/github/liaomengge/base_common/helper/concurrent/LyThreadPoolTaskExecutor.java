package com.github.liaomengge.base_common.helper.concurrent;

import com.github.liaomengge.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 18/1/2.
 */
@Slf4j
@Getter
@Setter
public class LyThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = 1180516546843511746L;

    private final Object poolSizeMonitor = new Object();

    private String threadName;

    private BlockingQueue<Runnable> blockingQueue;

    private boolean waitForTasksToCompleteOnShutdown = false;

    private int awaitTerminationSeconds = 0;

    private int checkInterval = 2;//默认：check时间间隔2s

    public LyThreadPoolTaskExecutor(String threadName) {
        this.threadName = threadName;
    }

    @Override
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        if (Objects.nonNull(this.blockingQueue)) {
            return blockingQueue;
        }
        this.blockingQueue = super.createQueue(queueCapacity);
        return blockingQueue;
    }

    @Override
    public void shutdown() {
        if (!this.waitForTasksToCompleteOnShutdown) {
            super.shutdown();
            return;
        }
        ThreadPoolExecutor executor = super.getThreadPoolExecutor();
        if (Objects.nonNull(executor)) {
            log.info("thread pool[{}] shutdown start...", threadName);
            executor.shutdown();
            if (this.awaitTerminationSeconds > 0) {
                try {
                    for (long remaining = this.awaitTerminationSeconds; remaining > 0; remaining -= this.checkInterval) {
                        try {
                            if (executor.awaitTermination(Math.min(remaining, this.checkInterval), TimeUnit.SECONDS)) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            log.warn("Interrupted while waiting for executor [{}] to terminate", threadName);
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (Exception e) {
                    log.info("thread pool[{}] shutdown exception", threadName, e);
                }
            }
            log.info("thread pool[{}] shutdown end...", threadName);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.setWaitForTasksToCompleteOnShutdown(this.waitForTasksToCompleteOnShutdown);
        super.setAwaitTerminationSeconds(this.awaitTerminationSeconds);
        if (StringUtils.isNotBlank(threadName)) {
            super.setThreadFactory(LyThreadFactoryBuilderUtil.build(threadName));
        }
        super.afterPropertiesSet();
    }
}
