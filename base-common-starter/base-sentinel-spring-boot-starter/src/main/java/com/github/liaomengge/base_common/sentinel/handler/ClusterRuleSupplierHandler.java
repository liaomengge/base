package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class ClusterRuleSupplierHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ruleSource =
                    ApolloDataSourceUtil.getInstance(SentinelClusterConst.FLOW_DATA_ID, Lists.newArrayList());
            return ruleSource.getProperty();
        });

        // Register cluster parameter flow rule property supplier which creates data source by namespace.
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ruleSource =
                    ApolloDataSourceUtil.getInstance(SentinelClusterConst.PARAM_FLOW_DATA_ID, Lists.newArrayList());
            return ruleSource.getProperty();
        });
    }
}
