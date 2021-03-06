package com.github.liaomengge.base_common.nacos.endpoint.process;

import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;

import java.util.Optional;
import java.util.Set;

/**
 * Created by liaomengge on 2020/8/17.
 */
public class NacosEndpointBeanPostProcess implements BeanPostProcessor {

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
                Set<String> pullSet = Sets.newHashSet(NacosConst.EndpointConst.PULL_IN,
                        NacosConst.EndpointConst.PULL_OUT);
                if (CollectionUtils.containsAll(includeSet, pullSet)) {
                    return;
                }
                includeSet.addAll(pullSet);
            });
        }
        return bean;
    }
}
