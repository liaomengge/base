package cn.ly.base_common.graceful.context;

import cn.ly.base_common.graceful.GracefulProperties;
import cn.ly.base_common.graceful.consts.GracefulConst;
import cn.mwee.base_common.utils.log4j2.MwLogger;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2019/2/22.
 */
public class ContextShutdown implements ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    private static final Logger logger = MwLogger.getInstance(ContextShutdown.class);

    private ApplicationContext applicationContext;
    private GracefulProperties gracefulProperties;

    public ContextShutdown(GracefulProperties gracefulProperties) {
        this.gracefulProperties = gracefulProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("context close start...");
        Map<String, ThreadPoolTaskExecutor> taskExecutorMap =
                applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
        if (MapUtils.isNotEmpty(taskExecutorMap)) {
            taskExecutorMap.forEach((key, value) -> {
                if (!value.getThreadPoolExecutor().isTerminating()) {
                    shutdown(key, value);
                }
            });
        }

        Map<String, ThreadPoolTaskScheduler> taskSchedulerMap =
                applicationContext.getBeansOfType(ThreadPoolTaskScheduler.class);
        if (MapUtils.isNotEmpty(taskSchedulerMap)) {
            taskSchedulerMap.forEach((key, value) -> {
                if (!value.getScheduledThreadPoolExecutor().isTerminating()) {
                    shutdown(key, value);
                }
            });
        }

        Map<String, ThreadPoolExecutor> executorMap = applicationContext.getBeansOfType(ThreadPoolExecutor.class);
        if (MapUtils.isNotEmpty(executorMap)) {
            executorMap.forEach((key, value) -> {
                if (!value.isTerminating()) {
                    shutdown(key, value);
                }
            });
        }
        logger.info("context close end...");
    }

    private void shutdown(String threadName, ThreadPoolTaskExecutor taskExecutor) {
        logger.info("thread pool task executor[{}] shutdown start...", threadName);
        try {
            for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                try {
                    if (taskExecutor.getThreadPoolExecutor().awaitTermination(Math.min(remaining, GracefulConst.CHECK_INTERVAL),
                            TimeUnit.SECONDS)) {
                        continue;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                logger.info("thread pool task executor[{}], {} thread(s) active, {} seconds remaining",
                        threadName, taskExecutor.getThreadPoolExecutor().getActiveCount(), remaining);
            }
        } catch (Exception e) {
            logger.info("thread pool task executor[" + threadName + "] shutdown exception", e);
        }
        logger.info("thread pool task executor[{}] shutdown end...", threadName);
    }

    private void shutdown(String threadName, ThreadPoolTaskScheduler taskScheduler) {
        logger.info("thread pool task scheduler[{}] shutdown start...", threadName);
        try {
            for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                try {
                    if (taskScheduler.getScheduledThreadPoolExecutor().awaitTermination(Math.min(remaining,
                            GracefulConst.CHECK_INTERVAL),
                            TimeUnit.SECONDS)) {
                        continue;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                logger.info("thread pool task scheduler[{}], {} thread(s) active, {} seconds remaining",
                        threadName, taskScheduler.getScheduledThreadPoolExecutor().getActiveCount(), remaining);
            }
        } catch (Exception e) {
            logger.info("thread pool task scheduler[" + threadName + "] shutdown exception", e);
        }
        logger.info("thread pool task scheduler[{}] shutdown end...", threadName);
    }

    private void shutdown(String threadName, ThreadPoolExecutor executor) {
        logger.info("thread pool executor[{}] shutdown start...", threadName);
        try {
            for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                try {
                    if (executor.awaitTermination(Math.min(remaining, GracefulConst.CHECK_INTERVAL),
                            TimeUnit.SECONDS)) {
                        continue;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                logger.info("thread pool task scheduler[{}], {} thread(s) active, {} seconds remaining",
                        threadName, executor.getActiveCount(), remaining);
            }
        } catch (Exception e) {
            logger.info("thread pool executor[" + threadName + "] shutdown exception", e);
        }
        logger.info("thread pool executor[{}] shutdown end...", threadName);
    }
}
