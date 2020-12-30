package com.github.liaomengge.base_common.ribbon.loadbalance;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.ribbon.RibbonProperties;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import com.github.liaomengge.base_common.utils.number.LyBigDecimalUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2020/12/28.
 * <p>
 * 1. 也可以继承{@link com.netflix.loadbalancer.PredicateBasedRule}
 */
public abstract class AbstractRibbonNacosRule extends AbstractPredicateRule {

    protected static final Logger log = LyLogger.getInstance(AbstractRibbonNacosRule.class);

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
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
            String name = loadBalancer.getName();

            NamingService namingService =
                    nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
            List<Instance> instances = namingService.selectInstances(name, group, true);
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("no instance in service {}", name);
                return null;
            }
            //依据集群过滤
            instances = filterSameCluster(name, clusterName, instances);
            //predicate过滤
            instances = Lists.newArrayList(Iterables.filter(instances, getPredicate()));
            //其他条件过滤
            instances = filterInstances(instances);
            if (CollectionUtils.isEmpty(instances)) {
                log.warn("no match instance in service {}", name);
                return null;
            }
            instances = rebuildInstanceWeight(instances);
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

    protected List<Instance> rebuildInstanceWeight(List<Instance> instances) {
        if (instances.size() == 1) {
            return instances;
        }
        return instances.stream().map(instance -> {
            instance.setWeight(getWeight(instance));
            return instance;
        }).collect(Collectors.toList());
    }

    protected double getWeight(Instance instance) {
        double weight = instance.getWeight();
        String registerTime = instance.getMetadata().get(NacosConst.MetadataConst.PRESERVED_REGISTER_TIME);
        if (StringUtils.isBlank(registerTime)) {
            return weight;
        }
        LocalDateTime registerLocalDateTime = LyJdk8DateUtil.getString2Date(registerTime);
        long startUpTime = ChronoUnit.MILLIS.between(registerLocalDateTime, LocalDateTime.now());
        startUpTime = Math.max(startUpTime, 1);
        long warmupTime = ribbonProperties.getWeightWarmup().getTime().toMillis();
        if (startUpTime < warmupTime) {
            weight = calculateWarmupWeight(startUpTime, warmupTime, weight);
        }
        return weight;
    }

    private double calculateWarmupWeight(long startUpTime, long warmupTime, double weight) {
        double calculateWeight = LyBigDecimalUtil.div(LyBigDecimalUtil.mul(startUpTime, weight), warmupTime);
        return Math.min(weight, Math.max(calculateWeight, NumberUtils.DOUBLE_ZERO));
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }

    @Override
    public Predicate<Instance> getPredicate() {
        return input -> true;
    }

    public List<Instance> filterInstances(List<Instance> instances) {
        return instances;
    }

    public abstract Instance chooseInstance(List<Instance> instances);
}
