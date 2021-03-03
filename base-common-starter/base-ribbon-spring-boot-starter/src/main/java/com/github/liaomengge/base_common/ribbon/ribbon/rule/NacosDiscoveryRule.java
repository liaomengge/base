package com.github.liaomengge.base_common.ribbon.ribbon.rule;

import com.github.liaomengge.base_common.ribbon.ribbon.predicate.NacosDiscoveryPredicate;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * Created by liaomengge on 2021/3/3.
 */
public class NacosDiscoveryRule extends AbstractDiscoveryRule {

    public NacosDiscoveryRule() {
        super(new NacosDiscoveryPredicate());
    }

    @Override
    public List<Server> filterServers(List<Server> serverList) {
        return serverList;
    }
}
