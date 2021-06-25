package com.github.liaomengge.base_common.mq.activemq.registry;

import com.github.liaomengge.base_common.mq.activemq.ActiveMQProperties;
import com.github.liaomengge.base_common.mq.activemq.domain.QueueConfig;
import com.github.liaomengge.base_common.utils.binder.LyBinderUtil;
import lombok.extern.slf4j.Slf4j;
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
 * Created by liaomengge on 2019/5/23.
 */
@Slf4j
public class ActiveMQQueueConfigBeanDefinitionRegistry implements EnvironmentAware,
        BeanDefinitionRegistryPostProcessor {
    
    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ActiveMQProperties activeMQProperties = LyBinderUtil.bind((ConfigurableEnvironment) environment,
                "base.mq.activemq", ActiveMQProperties.class);
        Optional.ofNullable(activeMQProperties).map(ActiveMQProperties::getQueues).ifPresent(queuePropertiesList -> {
            queuePropertiesList.forEach(queueProperties -> {
                BeanDefinitionBuilder builder =
                        BeanDefinitionBuilder.genericBeanDefinition(QueueConfig.class);
                builder.addConstructorArgValue(queueProperties.getBaseQueueName());
                builder.addConstructorArgValue(queueProperties.getQueueCount());
                BeanDefinition beanDefinition = builder.getRawBeanDefinition();
                ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(beanDefinition);
                beanDefinition.setScope(scopeMetadata.getScopeName());
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
                        queueProperties.buildBeanName());
                BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
            });
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
