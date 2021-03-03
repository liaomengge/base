package com.github.liaomengge.base_common.ribbon.ribbon.predicate;

import com.alibaba.cloud.nacos.ribbon.NacosServer;

/**
 * Created by liaomengge on 2021/3/3.
 */
public class NacosDiscoveryPredicate extends AbstractDiscoveryPredicate {

    @Override
    public boolean apply(NacosServer nacosServer) {
        return true;
    }
}
