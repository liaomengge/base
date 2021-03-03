package com.github.liaomengge.base_common.ribbon.ribbon.filter;

import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * Created by liaomengge on 2020/12/31.
 */
@FunctionalInterface
public interface ServerFilter {
    List<Server> apply(List<Server> servers);
}
