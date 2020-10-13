package com.github.liaomengge.base_common.thread.pool.registry;

import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolTaskExecutor;
import com.github.liaomengge.base_common.helper.concurrent.LyThreadPoolTaskWrappedExecutor;
import com.github.liaomengge.base_common.helper.concurrent.LyTtlThreadPoolTaskExecutor;
import com.github.liaomengge.base_common.thread.pool.ThreadPoolGroupProperties;
import com.github.liaomengge.base_common.utils.binder.LyBinderUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * Created by liaomengge on 2019/5/17.
 */
public class ThreadPoolBeanDefinitionRegistry implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ThreadPoolGroupProperties threadPoolGroupProperties = LyBinderUtil.bind((ConfigurableEnvironment) environment,
                "base.thread-pool", ThreadPoolGroupProperties.class);
        Optional.ofNullable(threadPoolGroupProperties).map(ThreadPoolGroupProperties::getGroups).ifPresent(threadPoolPropertiesList -> {
            threadPoolPropertiesList.forEach(threadPoolProperties -> {
                LyThreadPoolTaskExecutor LyThreadPoolTaskExecutor = buildThreadPool(threadPoolProperties);
                BeanDefinitionBuilder builder =
                        BeanDefinitionBuilder.genericBeanDefinition(LyThreadPoolTaskWrappedExecutor.class);
                builder.addConstructorArgValue(LyThreadPoolTaskExecutor);
                BeanDefinition beanDefinition = builder.getRawBeanDefinition();
                ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(beanDefinition);
                beanDefinition.setScope(scopeMetadata.getScopeName());
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
                        threadPoolProperties.buildBeanName());
                BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
            });
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private LyThreadPoolTaskExecutor buildThreadPool(ThreadPoolGroupProperties.ThreadPoolProperties threadPoolProperties) {
        LyThreadPoolTaskExecutor threadPoolTaskExecutor;
        if (!threadPoolProperties.isTtlEnabled()) {
            threadPoolTaskExecutor = new LyThreadPoolTaskExecutor(threadPoolProperties.buildThreadName());
        } else {
            threadPoolTaskExecutor = new LyTtlThreadPoolTaskExecutor(threadPoolProperties.buildThreadName());
        }
        fillThreadPoolParameter(threadPoolTaskExecutor, threadPoolProperties);
        return threadPoolTaskExecutor;
    }

    private void fillThreadPoolParameter(LyThreadPoolTaskExecutor threadPoolTaskExecutor,
                                         ThreadPoolGroupProperties.ThreadPoolProperties threadPoolProperties) {
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        threadPoolTaskExecutor.setBlockingQueue(threadPoolProperties.buildBlockingQueue());
        threadPoolTaskExecutor.setRejectedExecutionHandler(threadPoolProperties.buildRejectionPolicy());

        threadPoolTaskExecutor.setCheckInterval(threadPoolProperties.getCheckInterval());
        threadPoolTaskExecutor.setAwaitTerminationSeconds(threadPoolProperties.getAwaitTerminationSeconds());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.isWaitForTasksToCompleteOnShutdown());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(threadPoolProperties.isAllowCoreThreadTimeOut());

        threadPoolTaskExecutor.afterPropertiesSet();
    }
}
