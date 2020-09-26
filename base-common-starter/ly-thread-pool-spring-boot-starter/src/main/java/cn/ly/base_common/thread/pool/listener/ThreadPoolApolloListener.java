package cn.ly.base_common.thread.pool.listener;

import static cn.ly.base_common.support.misc.consts.ToolConst.SPLITTER;

import cn.ly.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import cn.ly.base_common.thread.pool.ThreadPoolGroupProperties;
import cn.ly.base_common.thread.pool.queue.ResizableCapacityLinkedBlockIngQueue;
import cn.ly.base_common.utils.binder.LyBinderUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by liaomengge on 2020/8/29.
 */
public class ThreadPoolApolloListener implements ApplicationContextAware, EnvironmentAware, ConfigChangeListener {

    private static final Logger log = LyLogger.getInstance(ThreadPoolApolloListener.class);

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
        Optional<String> threadPoolChange = changedKeys.stream().filter(val -> StringUtils.startsWithIgnoreCase(val,
                "ly.thread-pool")).findFirst();
        threadPoolChange.ifPresent(val -> {
            ThreadPoolGroupProperties threadPoolGroupProperties = LyBinderUtil.bind(environment, "ly.thread-pool",
                    ThreadPoolGroupProperties.class);
            threadPoolGroupProperties.getGroups().stream().forEach(val2 -> {
                LyThreadPoolTaskWrappedExecutor wrappedExecutor =
                        applicationContext.getBean(val2.buildBeanName(), LyThreadPoolTaskWrappedExecutor.class);
                if (Objects.isNull(wrappedExecutor)) {
                    log.warn("can't modify thread name, thread pool name[{}], don't exist!!!", val2.buildBeanName());
                    return;
                }
                /**
                 * ThreadPoolTaskExecutor的处理和ThreadPoolExecutor处理有点区别。
                 * springframework blockQueue没有直接暴露出来，且修改其属性的时候，需要修改两边的属性
                 */
                ThreadPoolTaskExecutor executor = wrappedExecutor.getThreadPoolTaskExecutor();
                executor.setCorePoolSize(val2.getCorePoolSize());
                executor.setMaxPoolSize(val2.getMaxPoolSize());
                executor.setKeepAliveSeconds(val2.getKeepAliveSeconds());
                setRejectedExecutionHandler(executor, val2.buildRejectionPolicy());
                setAllowCoreThreadTimeOut(executor, val2.isAllowCoreThreadTimeOut());
                setQueueCapacity(executor, val2.getQueueCapacity());
            });
        });
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
