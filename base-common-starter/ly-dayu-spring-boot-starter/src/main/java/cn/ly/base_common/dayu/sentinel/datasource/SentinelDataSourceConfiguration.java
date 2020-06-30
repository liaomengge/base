package cn.ly.base_common.dayu.sentinel.datasource;

import cn.ly.base_common.utils.json.MwJsonUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by liaomengge on 2019/11/7.
 */
@Configuration
public class SentinelDataSourceConfiguration {

    @Bean("giraffeFlowRuleDataSource")
    public GiraffeDataSource<List<FlowRule>> giraffeFlowRuleDataSource() {
        GiraffeDataSource<List<FlowRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<FlowRule>>) source -> MwJsonUtil.fromJson(source,
                new TypeReference<List<FlowRule>>() {
                }), "flows");
        return giraffeDataSource;
    }

    @Bean("giraffeDegradeRuleDataSource")
    public GiraffeDataSource<List<DegradeRule>> giraffeDegradeRuleDataSource() {
        GiraffeDataSource<List<DegradeRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<DegradeRule>>) source -> MwJsonUtil.fromJson(source,
                new TypeReference<List<DegradeRule>>() {
                }), "degrades");
        return giraffeDataSource;
    }

    @Bean("giraffeAuthorityRuleDataSource")
    public GiraffeDataSource<List<AuthorityRule>> giraffeAuthorityRuleDataSource() {
        GiraffeDataSource<List<AuthorityRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<AuthorityRule>>) source -> MwJsonUtil.fromJson(source,
                new TypeReference<List<AuthorityRule>>() {
                }), "authorities");
        return giraffeDataSource;
    }

    @Bean("giraffeParamFlowRuleDataSource")
    public GiraffeDataSource<List<ParamFlowRule>> giraffeParamFlowRuleDataSource() {
        GiraffeDataSource<List<ParamFlowRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<ParamFlowRule>>) source -> MwJsonUtil.fromJson(source,
                new TypeReference<List<ParamFlowRule>>() {
                }), "paramFlows");
        return giraffeDataSource;
    }

    @Bean("giraffeSystemRuleDataSource")
    public GiraffeDataSource<List<SystemRule>> giraffeSystemRuleDataSource() {
        GiraffeDataSource<List<SystemRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<SystemRule>>) source -> MwJsonUtil.fromJson(source,
                new TypeReference<List<SystemRule>>() {
                }), "systems");
        return giraffeDataSource;
    }
}
