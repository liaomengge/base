package com.github.liaomengge.base_common.apollo.refresh.conditional;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.github.liaomengge.base_common.apollo.consts.ApolloConst;
import com.github.liaomengge.base_common.apollo.enums.RefreshTypeEnum;
import com.github.liaomengge.base_common.apollo.refresh.conditional.manager.ConditionalOnPropertyManager;
import com.github.liaomengge.base_common.apollo.refresh.conditional.pojo.ConditionalOnPropertyDomain;
import com.github.liaomengge.base_common.support.spring.SpringUtils;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Created by liaomengge on 2021/1/29.
 */
@Slf4j
public class ApolloConditionalRefresh implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final ConditionalOnPropertyManager conditionalOnPropertyManager;

    public ApolloConditionalRefresh(ConditionalOnPropertyManager conditionalOnPropertyManager) {
        this.conditionalOnPropertyManager = conditionalOnPropertyManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void refresh(ConfigChangeEvent changeEvent) {
        try {
            RefreshTypeEnum refreshTypeEnum = getRefreshTypeEnum(changeEvent);
            if (refreshTypeEnum == RefreshTypeEnum.PROPERTIES || refreshTypeEnum == RefreshTypeEnum.ALL) {
                Map<ConditionalOnPropertyDomain, ConditionalOnProperty> conditionalClassMap =
                        conditionalOnPropertyManager.getConditionalBeanMap();

                conditionalClassMap.entrySet().stream().map(entry -> {
                    ConditionalOnPropertySpec spec = new ConditionalOnPropertySpec(entry.getValue());
                    spec.setBeanName(entry.getKey().getBeanName());
                    spec.setBeanClass(entry.getKey().getBeanClass());
                    return spec;
                }).filter(spec -> ArrayUtils.isNotEmpty(spec.getNames()))
                        .forEach(spec -> dynamicChangeBean(changeEvent, spec));
            }
        } catch (Exception e) {
            log.error("apollo refresh conditional fail", e);
        }
    }

    private void dynamicChangeBean(ConfigChangeEvent changeEvent, ConditionalOnPropertySpec spec) {
        String key = getConditionalOnPropertyName(changeEvent, spec);
        String value = spec.getHavingValue();
        String beanName = spec.getBeanName();
        if (StringUtils.isBlank(key)) {
            return;
        }
        if (isNeedRegisterForKey(changeEvent, key, value)) {
            if (SpringUtils.isExistBean(beanName)) {
                log.info("not need registry, bean class name[{}] is exist ...", beanName);
                return;
            }
            SpringUtils.registerBean(spec.getBeanName(), spec.getBeanClass());
            log.info("register bean class name[{}] ...", beanName);
            return;
        }
        if (isRemoveRegisterForKey(changeEvent, key, value)) {
            if (!SpringUtils.isExistBean(beanName)) {
                log.info("not need remove registry, bean class name[{}] is not exist ...", beanName);
                return;
            }
            //同时会destory依赖的bean，不能用@Autowired(required = false)注解使用
            SpringUtils.unregisterBean(beanName);
            log.info("unregister bean class name[{}] ...", beanName);
            return;
        }
    }

    private RefreshTypeEnum getRefreshTypeEnum(ConfigChangeEvent changeEvent) {
        Set<String> changeKeys = changeEvent.changedKeys();
        if (CollectionUtils.containsAny(changeKeys, ApolloConst.REFRESH_TYPE_KEY)) {
            ConfigChange configChange = changeEvent.getChange(ApolloConst.REFRESH_TYPE_KEY);
            return Optional.ofNullable(configChange).map(ConfigChange::getNewValue)
                    .map(RefreshTypeEnum::getInstance).orElse(RefreshTypeEnum.PROPERTIES);
        }

        return RefreshTypeEnum.getInstance(applicationContext.getEnvironment().getProperty(ApolloConst.REFRESH_TYPE_KEY));
    }

    private String getConditionalOnPropertyName(ConfigChangeEvent changeEvent,
                                                ConditionalOnPropertySpec conditionalOnPropertySpec) {
        Set<String> changeKeys = changeEvent.changedKeys();
        String[] name = conditionalOnPropertySpec.getNames();
        if (ArrayUtils.isEmpty(name)) {
            return null;
        }
        return Arrays.stream(name).filter(changeKeys::contains).findFirst().orElse(null);
    }

    private boolean isNeedRegisterForKey(ConfigChangeEvent changeEvent, String key, String value) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        ConfigChange configChange = changeEvent.getChange(key);
        String changeValue = configChange.getNewValue();
        if ("".equals(value) && !"false".equals(changeValue)) {
            return true;
        }
        return StringUtils.equals(changeValue, value);
    }

    private boolean isRemoveRegisterForKey(ConfigChangeEvent changeEvent, String key, String value) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        ConfigChange configChange = changeEvent.getChange(key);
        String changeValue = configChange.getNewValue();
        if ("".equals(value) && "false".equals(changeValue)) {
            return true;
        }
        return !StringUtils.equals(changeValue, value);
    }

    @Data
    private static class ConditionalOnPropertySpec {

        private String beanName;
        private Class<?> beanClass;
        private final String prefix;
        private final String[] names;
        private final String havingValue;
        private final boolean matchIfMissing;

        public ConditionalOnPropertySpec(ConditionalOnProperty conditionalOnProperty) {
            this.prefix = StringUtils.defaultIfBlank(conditionalOnProperty.prefix(), "");
            this.names = this.getName(conditionalOnProperty);
            this.havingValue = conditionalOnProperty.havingValue();
            this.matchIfMissing = conditionalOnProperty.matchIfMissing();
        }

        private String[] getName(ConditionalOnProperty conditionalOnProperty) {
            String[] value = conditionalOnProperty.value();
            String[] name = conditionalOnProperty.name();
            Set<String> result = (value.length > 0) ? Sets.newHashSet(value) : Sets.newHashSet(name);
            if (CollectionUtils.isEmpty(result)) {
                return null;
            }
            if (StringUtils.isBlank(prefix)) {
                return result.stream().toArray(String[]::new);
            }
            return result.stream().map(val -> this.prefix + '.' + val).toArray(String[]::new);
        }
    }
}
