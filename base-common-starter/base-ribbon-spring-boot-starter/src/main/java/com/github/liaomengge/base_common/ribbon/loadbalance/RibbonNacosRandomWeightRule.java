package com.github.liaomengge.base_common.ribbon.loadbalance;

import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * Created by liaomengge on 2020/12/28.
 */
public class RibbonNacosRandomWeightRule extends AbstractRibbonNacosRule {

    @Override
    public Instance chooseInstance(List<Instance> instances) {
        return ExtendBalancer.getHostByRandomWeight2(instances);
    }
}
