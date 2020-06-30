package cn.ly.base_common.mq.activemq.registry;

import cn.ly.base_common.mq.activemq.ActiveMQProperties;
import cn.ly.base_common.mq.activemq.domain.QueueConfig;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.log4j2.MwLogger;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
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
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2019/5/23.
 */
public class ActiveMQQueueConfigBeanDefinitionRegistry implements EnvironmentAware,
        BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = MwLogger.getInstance(ActiveMQQueueConfigBeanDefinitionRegistry.class);

    private final ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<String, LinkedHashMap<String, Object>> queuePropertiesMap = Maps.newHashMap();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        new RelaxedDataBinder(queuePropertiesMap, "ly.mq.activemq").bind(new PropertySourcesPropertyValues(propertySources));
        LinkedHashMap<String, Object> subQueuePropertiesMap = queuePropertiesMap.get("queues");
        subQueuePropertiesMap = subQueuePropertiesMap.entrySet().stream()
                .filter(val -> val.getValue() instanceof LinkedHashMap && StringUtils.isNumeric(val.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal,
                        LinkedHashMap::new));
        if (MapUtils.isEmpty(subQueuePropertiesMap)) {
            logger.info("ActiveMQ初始化, 暂未配置初始化队列...");
            return;
        }
        String queueJson = MwJsonUtil.toJson(subQueuePropertiesMap);
        Map<String, ActiveMQProperties.QueueProperties> queueJsonMap = MwJsonUtil.fromJson(queueJson,
                new TypeReference<Map<String, ActiveMQProperties.QueueProperties>>() {
                });
        queueJsonMap.values().forEach(queueProperties -> {
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
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
