package com.github.liaomengge.base_common.eureka.endpoint.process;

import com.github.liaomengge.base_common.eureka.consts.EurekaConst;

import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;

/**
 * Created by liaomengge on 2020/8/17.
 */
public class EurekaEndpointBeanPostProcess implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebEndpointProperties) {
            WebEndpointProperties endpointProperties = (WebEndpointProperties) bean;
            Optional.ofNullable(endpointProperties.getExposure()).ifPresent(val -> {
                Set<String> includeSet = val.getInclude();
                if (CollectionUtils.isEmpty(includeSet)) {
                    includeSet.addAll(Sets.newHashSet("info", "health"));
                }
                if (CollectionUtils.containsAny(includeSet, "*")) {
                    return;
                }
                Set<String> pullSet = Sets.newHashSet(EurekaConst.PULL_IN_ENDPOINT, EurekaConst.PULL_OUT_ENDPOINT);
                if (CollectionUtils.containsAll(includeSet, pullSet)) {
                    return;
                }
                includeSet.addAll(pullSet);
            });
        }
        return bean;
    }
}
