package com.github.liaomengge.base_common.sentinel.handler;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.util.ApolloDataSourceUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by liaomengge on 2021/1/12.
 */
public class DynamicRuleHandler extends AbstractSentinelHandler {

    @Override
    public void doInit() throws Exception {
        ReadableDataSource<String, List<FlowRule>> ruleDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.FLOW_DATA_ID, Lists.newArrayList());
        FlowRuleManager.register2Property(ruleDs.getProperty());
        log.info(SentinelClusterConst.FLOW_DATA_ID + " => {}", ruleDs.readSource());

        ReadableDataSource<String, List<ParamFlowRule>> paramRuleDs =
                ApolloDataSourceUtil.getInstance(SentinelClusterConst.PARAM_FLOW_DATA_ID, Lists.newArrayList());
        ParamFlowRuleManager.register2Property(paramRuleDs.getProperty());
        log.info(SentinelClusterConst.PARAM_FLOW_DATA_ID + " => {}", paramRuleDs.readSource());
    }
}
