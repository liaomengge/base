package cn.mwee.base_common.thread.pool.registry;

import cn.mwee.base_common.helper.concurrent.MwThreadPoolTaskExecutor;
import cn.mwee.base_common.helper.concurrent.MwThreadPoolTaskWrappedExecutor;
import cn.mwee.base_common.helper.concurrent.MwTtlThreadPoolTaskExecutor;
import cn.mwee.base_common.thread.pool.ThreadPoolGroupProperties.ThreadPoolProperties;
import cn.mwee.base_common.utils.json.MwJsonUtil;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Map<String, LinkedHashMap<String, Object>> threadPoolPropertiesMap = Maps.newHashMap();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        new RelaxedDataBinder(threadPoolPropertiesMap, "mwee.thread-pool").bind(new PropertySourcesPropertyValues(propertySources));
        LinkedHashMap<String, Object> subThreadPoolPropertiesMap = threadPoolPropertiesMap.get("groups");
        subThreadPoolPropertiesMap =
                subThreadPoolPropertiesMap.entrySet().stream()
                        .filter(val -> val.getValue() instanceof LinkedHashMap && StringUtils.isNumeric(val.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal,
                                LinkedHashMap::new));
        String threadPoolJson = MwJsonUtil.toJson(subThreadPoolPropertiesMap);
        Map<String, ThreadPoolProperties> threadPoolJsonMap = MwJsonUtil.fromJson(threadPoolJson,
                new TypeReference<Map<String, ThreadPoolProperties>>() {
                });
        Optional.ofNullable(threadPoolJsonMap).ifPresent(val -> val.values().forEach(threadPoolProperties -> {
            MwThreadPoolTaskExecutor mwThreadPoolTaskExecutor = buildThreadPool(threadPoolProperties);
            BeanDefinitionBuilder builder =
                    BeanDefinitionBuilder.genericBeanDefinition(MwThreadPoolTaskWrappedExecutor.class);
            builder.addConstructorArgValue(mwThreadPoolTaskExecutor);
            BeanDefinition beanDefinition = builder.getRawBeanDefinition();
            ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(beanDefinition);
            beanDefinition.setScope(scopeMetadata.getScopeName());
            BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition,
                    threadPoolProperties.buildBeanName());
            BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);
        }));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    private MwThreadPoolTaskExecutor buildThreadPool(ThreadPoolProperties threadPoolProperties) {
        MwThreadPoolTaskExecutor threadPoolTaskExecutor;
        if (!threadPoolProperties.isTtlEnabled()) {
            threadPoolTaskExecutor = new MwThreadPoolTaskExecutor(threadPoolProperties.buildThreadName());
        } else {
            threadPoolTaskExecutor = new MwTtlThreadPoolTaskExecutor(threadPoolProperties.buildThreadName());
        }
        fillThreadPoolParameter(threadPoolTaskExecutor, threadPoolProperties);
        return threadPoolTaskExecutor;
    }

    private void fillThreadPoolParameter(MwThreadPoolTaskExecutor threadPoolTaskExecutor,
                                         ThreadPoolProperties threadPoolProperties) {
        threadPoolTaskExecutor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        threadPoolTaskExecutor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        threadPoolTaskExecutor.setCheckInterval(threadPoolProperties.getCheckInterval());
        threadPoolTaskExecutor.setAwaitTerminationSeconds(threadPoolProperties.getAwaitTerminationSeconds());
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(threadPoolProperties.isWaitForTasksToCompleteOnShutdown());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(threadPoolProperties.isAllowCoreThreadTimeOut());
        threadPoolTaskExecutor.setRejectedExecutionHandler(threadPoolProperties.buildRejectionPolicy());
        threadPoolTaskExecutor.afterPropertiesSet();
    }
}
