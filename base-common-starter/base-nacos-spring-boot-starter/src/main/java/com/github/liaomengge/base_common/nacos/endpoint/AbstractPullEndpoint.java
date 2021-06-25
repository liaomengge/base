package com.github.liaomengge.base_common.nacos.endpoint;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaomengge on 2020/8/17.
 */
public abstract class AbstractPullEndpoint {

    protected static final Logger log = LoggerFactory.getLogger(AbstractPullEndpoint.class);

    protected Instance getNacosInstance(NacosRegistration registration, boolean enabled) {
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setWeight(registration.getRegisterWeight());
        instance.setClusterName(registration.getCluster());
        instance.setEnabled(enabled);
        instance.setMetadata(registration.getMetadata());
        instance.setEphemeral(registration.getNacosDiscoveryProperties().isEphemeral());
        return instance;
    }
}
