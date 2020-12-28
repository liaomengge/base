package com.github.liaomengge.base_common.nacos.endpoint;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 2020/8/17.
 */
public abstract class AbstractPullEndpoint {

    protected static final Logger log = LyLogger.getInstance(AbstractPullEndpoint.class);

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
