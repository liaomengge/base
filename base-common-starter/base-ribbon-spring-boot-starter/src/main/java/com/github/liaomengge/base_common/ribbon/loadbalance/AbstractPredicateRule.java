package com.github.liaomengge.base_common.ribbon.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.base.Predicate;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;

/**
 * Created by liaomengge on 2020/12/30.
 */
public abstract class AbstractPredicateRule extends AbstractLoadBalancerRule {

    public abstract Predicate<Instance> getPredicate();
}
