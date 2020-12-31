package com.github.liaomengge.base_common.ribbon.loadbalance.filter;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * Created by liaomengge on 2020/12/31.
 */
@FunctionalInterface
public interface ServerFilter {
    List<Instance> apply(List<Instance> instances);
}
