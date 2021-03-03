package com.github.liaomengge.base_common.ribbon.ribbon.predicate;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Created by liaomengge on 2021/3/3.
 */
public abstract class AbstractDiscoveryPredicate extends AbstractServerPredicate {

    @Override
    public boolean apply(@Nullable PredicateKey input) {
        return input != null && input.getServer() instanceof NacosServer && apply((NacosServer) input.getServer());
    }

    public abstract boolean apply(NacosServer nacosServer);
}
