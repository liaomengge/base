package com.github.liaomengge.base_common.thread.pool.listener;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import com.github.liaomengge.base_common.thread.pool.ThreadPoolGroupProperties;
import com.github.liaomengge.base_common.thread.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import com.github.liaomengge.base_common.utils.binder.LyBinderUtil;
import com.github.liaomengge.base_common.utils.regex.LyMatcherUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.stream.Collectors;

import static com.github.liaomengge.base_common.support.misc.consts.ToolConst.SPLITTER;

/**
 * Created by liaomengge on 2020/8/29.
 */
@Slf4j
public class ThreadPoolApolloListener implements ApplicationContextAware, EnvironmentAware, ConfigChangeListener {
    
    @Value("${apollo.bootstrap.namespaces:application}")
    private String namespaces;

    private Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        SPLITTER.split(this.namespaces).forEach(namespace -> {
            Config config = ConfigService.getConfig(namespace);
            config.addChangeListener(this);
        });
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> changedKeys = changeEvent.changedKeys();
        Set<String> threadPoolChanges = changedKeys.stream().filter(val -> StringUtils.startsWithIgnoreCase(val,
                "base.thread-pool")).collect(Collectors.toSet());
        for (String threadPoolChange : threadPoolChanges) {
            String threadPoolChangePrefix = StringUtils.substring(threadPoolChange, 0,
                    threadPoolChange.lastIndexOf('.'));
            if (LyMatcherUtil.isAllMatch("^base.thread-pool.groups\\[\\d+\\]$", threadPoolChangePrefix)) {
                ThreadPoolGroupProperties.ThreadPoolProperties threadPoolProperties = LyBinderUtil.bind(environment,
                        threadPoolChangePrefix, ThreadPoolGroupProperties.ThreadPoolProperties.class);
                if (Objects.nonNull(threadPoolProperties)) {
                    LyThreadPoolTaskWrappedExecutor wrappedExecutor =
                            applicationContext.getBean(threadPoolProperties.buildBeanName(),
                                    LyThreadPoolTaskWrappedExecutor.class);
                    if (Objects.isNull(wrappedExecutor)) {
                        log.warn("can't modify thread name, thread pool name[{}], don't exist!!!",
                                threadPoolProperties.buildBeanName());
                        continue;
                    }
                    /**
                     * ThreadPoolTaskExecutor的处理和ThreadPoolExecutor处理有点区别。
                     * springframework blockQueue没有直接暴露出来，且修改其属性的时候，需要修改两边的属性
                     */
                    ThreadPoolTaskExecutor executor = wrappedExecutor.getThreadPoolTaskExecutor();
                    executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
                    executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
                    executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
                    setRejectedExecutionHandler(executor, threadPoolProperties.buildRejectionPolicy());
                    setAllowCoreThreadTimeOut(executor, threadPoolProperties.isAllowCoreThreadTimeOut());
                    setQueueCapacity(executor, threadPoolProperties.getQueueCapacity());
                }
            }
        }
    }

    private void setRejectedExecutionHandler(ThreadPoolTaskExecutor executor,
                                             RejectedExecutionHandler rejectedExecutionHandler) {
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        Optional.ofNullable(executor.getThreadPoolExecutor()).ifPresent(val -> val.setRejectedExecutionHandler(rejectedExecutionHandler));
    }

    private void setAllowCoreThreadTimeOut(ThreadPoolTaskExecutor executor,
                                           boolean allowCoreThreadTimeOut) {
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        Optional.ofNullable(executor.getThreadPoolExecutor()).ifPresent(val -> val.allowCoreThreadTimeOut(allowCoreThreadTimeOut));
    }

    private void setQueueCapacity(ThreadPoolTaskExecutor executor, int queueCapacity) {
        if (Objects.nonNull(executor.getThreadPoolExecutor())) {
            BlockingQueue<Runnable> blockingQueue = executor.getThreadPoolExecutor().getQueue();
            if (blockingQueue instanceof ResizableCapacityLinkedBlockIngQueue) {
                executor.setQueueCapacity(queueCapacity);
                ((ResizableCapacityLinkedBlockIngQueue<Runnable>) blockingQueue).setCapacity(queueCapacity);
            }
        }
    }
}
