package cn.ly.base_common.helper.concurrent;

import cn.ly.base_common.utils.log4j2.LyLogger;
import cn.ly.base_common.utils.thread.LyThreadFactoryBuilderUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 18/1/2.
 */
@Getter
@Setter
public class LyThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final Logger log = LyLogger.getInstance(LyThreadPoolTaskExecutor.class);
    private static final long serialVersionUID = 1180516546843511746L;

    private String threadName;

    private boolean waitForTasksToCompleteOnShutdown = false;

    private int awaitTerminationSeconds = 0;

    private int checkInterval = 2;//默认：check时间间隔2s

    public LyThreadPoolTaskExecutor(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void shutdown() {
        if (!this.waitForTasksToCompleteOnShutdown || this.awaitTerminationSeconds <= 0) {
            super.setWaitForTasksToCompleteOnShutdown(this.waitForTasksToCompleteOnShutdown);
            super.setAwaitTerminationSeconds(this.awaitTerminationSeconds);
            super.shutdown();
            return;
        }
        ThreadPoolExecutor executor = super.getThreadPoolExecutor();
        if (Objects.nonNull(executor)) {
            log.info("thread pool[{}] shutdown start...", threadName);
            executor.shutdown();
            try {
                for (long remaining = this.awaitTerminationSeconds; remaining > 0; remaining -= this.checkInterval) {
                    try {
                        if (executor.awaitTermination(Math.min(remaining, this.checkInterval), TimeUnit.SECONDS)) {
                            continue;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.info("thread pool[" + threadName + "] shutdown exception", e);
            }
            log.info("thread pool[{}] shutdown end...", threadName);
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(threadName)) {
            super.setThreadFactory(LyThreadFactoryBuilderUtil.build(threadName));
        }
        super.afterPropertiesSet();
    }
}
