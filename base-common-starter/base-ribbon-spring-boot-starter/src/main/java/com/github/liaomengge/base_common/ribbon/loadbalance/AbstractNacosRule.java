package com.github.liaomengge.base_common.ribbon.loadbalance;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.ribbon.RibbonProperties;
import com.github.liaomengge.base_common.ribbon.loadbalance.filter.InstanceFilter;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/12/28.
 * <p>
 * 1. 此处不走ribbon缓存
 * 2. 走ribbon缓存，见{@link com.github.liaomengge.base_common.ribbon.ribbon.rule.AbstractDiscoveryRule#choose(Object)}
 */
public abstract class AbstractNacosRule extends AbstractPredicateRule {

    protected static final Logger log = LyLogger.getInstance(AbstractNacosRule.class);

    @Getter
    private IClientConfig clientConfig;

    @Autowired(required = false)
    protected List<InstanceFilter> instanceFilters;

    @Autowired
    protected RibbonProperties ribbonProperties;

    @Autowired
    protected NacosServiceManager nacosServiceManager;

    @Autowired
    protected NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public Server choose(Object key) {
        try {
            String clusterName = this.nacosDiscoveryProperties.getClusterName();
            String group = this.nacosDiscoveryProperties.getGroup();
            String serviceName = this.getServiceName();
            if (StringUtils.isBlank(serviceName)) {
                return null;
            }
            NamingService namingService =
                    nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
            List<Instance> instances = namingService.selectInstances(serviceName, group, true);
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("no instance in service {}", serviceName);
                return null;
            }
            //依据集群过滤
            instances = filterSameCluster(serviceName, clusterName, instances);
            //predicate过滤
            instances = Lists.newArrayList(Iterables.filter(instances, getPredicate()));
            //其他条件过滤
            instances = filterInstances(instances);
            //自定义扩展filter
            if (CollectionUtils.isNotEmpty(instanceFilters)) {
                for (InstanceFilter instanceFilter : instanceFilters) {
                    instances = instanceFilter.apply(instances);
                }
            }
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("no match instance in service {}", serviceName);
                return null;
            }
            return new NacosServer(chooseInstance(instances));
        } catch (Exception e) {
            log.warn("choose nacos server error", e);
            return null;
        }
    }

    protected List<Instance> filterSameCluster(String serviceName, String clusterName, List<Instance> instances) {
        if (StringUtils.isNotBlank(clusterName)) {
            List<Instance> sameClusterInstances = instances.stream().filter(instance -> Objects.equals(clusterName,
                    instance.getClusterName())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sameClusterInstances)) {
                return sameClusterInstances;
            }
            log.warn("A cross-cluster call occurs，name = {}, clusterName = {}, instance = {}", serviceName, clusterName,
                    instances);
        }
        return instances;
    }

    @Override
    public Predicate<Instance> getPredicate() {
        return input -> true;
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

    public List<Instance> filterInstances(List<Instance> instances) {
        return instances;
    }

    public abstract Instance chooseInstance(List<Instance> instances);
}
