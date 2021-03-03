package com.github.liaomengge.base_common.ribbon.loadbalance.metadata;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.liaomengge.base_common.ribbon.loadbalance.NacosRandomWeightRule;
import com.google.common.base.Predicate;

/**
 * Created by liaomengge on 2020/12/30.
 */
public class MetaDataTagRule extends NacosRandomWeightRule {

    private Predicate<Instance> predicate;

    public MetaDataTagRule() {
        this.predicate = new MetaDataTagPredicate();
    }

    @Override
    public Predicate<Instance> getPredicate() {
        return this.predicate;
    }
}
