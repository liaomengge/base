package com.github.liaomengge.base_common.apollo.refresh.conditional.manager;

import com.github.liaomengge.base_common.apollo.ApolloProperties;
import com.github.liaomengge.base_common.apollo.ApolloProperties.BeanConditionalProperties;
import com.github.liaomengge.base_common.apollo.ApolloProperties.ClassConditionalProperties;
import com.github.liaomengge.base_common.apollo.refresh.conditional.pojo.ConditionalOnPropertyDomain;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2021/1/29.
 */
public class ConditionalOnPropertyManager {

    private static final Logger log = LyLogger.getInstance(ConditionalOnPropertyManager.class);

    @Getter
    public Map<ConditionalOnPropertyDomain, ConditionalOnProperty> conditionalBeanMap = Maps.newConcurrentMap();

    private final ApolloProperties apolloProperties;

    public ConditionalOnPropertyManager(ApolloProperties apolloProperties) {
        this.apolloProperties = apolloProperties;
    }

    @PostConstruct
    public void init() {
        List<ClassConditionalProperties> classConditionalPropertiesList =
                apolloProperties.getConditional().getClassConditionals();
        for (ClassConditionalProperties classConditionalProperties : classConditionalPropertiesList) {
            String scanClassName = classConditionalProperties.getScanClassName();
            try {
                Class<?> clazz = ClassUtils.getClass(scanClassName);
                Set<Method> methodSet = ReflectionUtils.getMethods(clazz,
                        method -> Objects.nonNull(method.getDeclaredAnnotation(ConditionalOnProperty.class)));
                List<BeanMethodConditionalProperties> convertBeanMethodConditionalProperties =
                        methodSet.stream().filter(Objects::nonNull).map(this::convertToBeanMethodConditionalProperties).collect(Collectors.toList());
                List<BeanConditionalProperties> beanConditionalPropertiesList =
                        classConditionalProperties.getBeanConditionals();
                for (BeanMethodConditionalProperties beanMethodConditionalProperties :
                        convertBeanMethodConditionalProperties) {
                    for (BeanConditionalProperties beanConditionalProperties : beanConditionalPropertiesList) {
                        if (StringUtils.equals(beanMethodConditionalProperties.getMethod().getName(),
                                beanConditionalProperties.getMethodName())) {
                            beanMethodConditionalProperties.setBeanName(beanConditionalProperties.getBeanName());
                            break;
                        }
                    }
                }
                convertBeanMethodConditionalProperties.stream().forEach(beanMethodConditionalProperties -> {
                    String beanName = beanMethodConditionalProperties.getBeanName();
                    Method method = beanMethodConditionalProperties.getMethod();

                    ConditionalOnProperty conditionalOnProperty =
                            method.getDeclaredAnnotation(ConditionalOnProperty.class);

                    ConditionalOnPropertyDomain conditionalOnPropertyDomain = new ConditionalOnPropertyDomain();
                    conditionalOnPropertyDomain.setBeanName(beanName);
                    conditionalOnPropertyDomain.setBeanClass(method.getReturnType());

                    conditionalBeanMap.put(conditionalOnPropertyDomain, conditionalOnProperty);
                });
            } catch (ClassNotFoundException e) {
                log.warn("config scan class name incorrect", e);
            }
        }
    }

    private BeanMethodConditionalProperties convertToBeanMethodConditionalProperties(Method method) {
        BeanMethodConditionalProperties beanMethodConditionalProperties = new BeanMethodConditionalProperties();
        beanMethodConditionalProperties.setMethod(method);
        beanMethodConditionalProperties.setBeanName(getBeanName(method));
        return beanMethodConditionalProperties;
    }

    private String getBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        if (Objects.isNull(bean)) {
            return null;
        }
        String[] result = bean.value().length > 0 ? bean.value() : bean.name();
        if (ArrayUtils.isNotEmpty(result)) {
            return result[0];
        }
        return StringUtils.uncapitalize(method.getReturnType().getSimpleName());
    }

    @Data
    private static class BeanMethodConditionalProperties {
        private Method method;
        private String beanName;
    }
}
