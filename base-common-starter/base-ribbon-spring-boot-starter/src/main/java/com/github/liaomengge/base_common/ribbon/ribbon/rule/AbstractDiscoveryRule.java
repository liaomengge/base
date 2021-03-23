package com.github.liaomengge.base_common.ribbon.ribbon.rule;

import com.github.liaomengge.base_common.ribbon.ribbon.filter.ServerFilter;
import com.github.liaomengge.base_common.ribbon.ribbon.predicate.AbstractDiscoveryPredicate;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.google.common.base.Optional;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.List;

/**
 * Created by liaomengge on 2021/3/3.
 */
public abstract class AbstractDiscoveryRule extends PredicateBasedRule {

    protected static final Logger log = LyLogger.getInstance(AbstractDiscoveryRule.class);

    @Getter
    private IClientConfig clientConfig;
    @Setter
    private List<ServerFilter> serverFilters;

    private CompositePredicate compositePredicate;
    private AbstractDiscoveryPredicate discoveryPredicate;

    public AbstractDiscoveryRule(AbstractDiscoveryPredicate discoveryPredicate) {
        super();
        this.discoveryPredicate = discoveryPredicate;
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, null);
        compositePredicate = createCompositePredicate(this.discoveryPredicate, availabilityPredicate);
    }

    private CompositePredicate createCompositePredicate(AbstractDiscoveryPredicate p1, AvailabilityPredicate p2) {
        return CompositePredicate.withPredicates(p1, p2).build();
    }

    @Override
    public Server choose(Object key) {
        try {
            ILoadBalancer lb = getLoadBalancer();

            List<Server> allServers = lb.getAllServers();
            //其他过滤
            List<Server> serverList = filterServers(allServers);
            //自定义扩展filter
            if (CollectionUtils.isNotEmpty(serverFilters)) {
                for (ServerFilter serverFilter : serverFilters) {
                    serverList = serverFilter.apply(serverList);
                }
            }
            Optional<Server> serverOptional = getPredicate().chooseRoundRobinAfterFiltering(serverList, key);
            if (serverOptional.isPresent()) {
                return serverOptional.get();
            }
            log.warn("for server list[{}], no match server in service {}", serverList, this.getServiceName());
        } catch (Exception e) {
            log.warn("choose server error", e);
        }
        return null;
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return this.compositePredicate;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    protected String getServiceName() {
        String serviceName = StringUtils.EMPTY;
        ILoadBalancer lb = getLoadBalancer();
        if (lb instanceof DynamicServerListLoadBalancer) {
            serviceName = ((DynamicServerListLoadBalancer) lb).getName();
        }
        return serviceName;
    }

    public abstract List<Server> filterServers(List<Server> serverList);
}
