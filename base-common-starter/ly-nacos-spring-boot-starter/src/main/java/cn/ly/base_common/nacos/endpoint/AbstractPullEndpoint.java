package cn.ly.base_common.nacos.endpoint;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * Created by liaomengge on 2020/8/17.
 */
public abstract class AbstractPullEndpoint {

    protected Instance getNacosInstance(NacosRegistration registration, boolean enabled) {
        Instance instance = new Instance();
        instance.setIp(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setWeight(registration.getRegisterWeight());
        instance.setClusterName(registration.getCluster());
        instance.setMetadata(registration.getMetadata());
        instance.setEnabled(enabled);
        return instance;
    }
}
