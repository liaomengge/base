package cn.ly.base_common.dayu.sentinel.datasource;

import cn.ly.base_common.utils.json.LyJacksonUtil;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/11/7.
 */
@Configuration
public class SentinelDataSourceConfiguration {

    @Bean("giraffeFlowRuleDataSource")
    public GiraffeDataSource<List<FlowRule>> giraffeFlowRuleDataSource() {
        GiraffeDataSource<List<FlowRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<FlowRule>>) source -> {
            try {
                return LyJacksonUtil.fromJson(source, new TypeReference<List<FlowRule>>() {
                });
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        }, "flows");
        return giraffeDataSource;
    }

    @Bean("giraffeDegradeRuleDataSource")
    public GiraffeDataSource<List<DegradeRule>> giraffeDegradeRuleDataSource() {
        GiraffeDataSource<List<DegradeRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<DegradeRule>>) source -> {
            try {
                return LyJacksonUtil.fromJson(source, new TypeReference<List<DegradeRule>>() {
                });
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        }, "degrades");
        return giraffeDataSource;
    }

    @Bean("giraffeAuthorityRuleDataSource")
    public GiraffeDataSource<List<AuthorityRule>> giraffeAuthorityRuleDataSource() {
        GiraffeDataSource<List<AuthorityRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<AuthorityRule>>) source -> {
            try {
                return LyJacksonUtil.fromJson(source, new TypeReference<List<AuthorityRule>>() {
                });
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        }, "authorities");
        return giraffeDataSource;
    }

    @Bean("giraffeParamFlowRuleDataSource")
    public GiraffeDataSource<List<ParamFlowRule>> giraffeParamFlowRuleDataSource() {
        GiraffeDataSource<List<ParamFlowRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<ParamFlowRule>>) source -> {
            try {
                return LyJacksonUtil.fromJson(source, new TypeReference<List<ParamFlowRule>>() {
                });
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        }, "paramFlows");
        return giraffeDataSource;
    }

    @Bean("giraffeSystemRuleDataSource")
    public GiraffeDataSource<List<SystemRule>> giraffeSystemRuleDataSource() {
        GiraffeDataSource<List<SystemRule>> giraffeDataSource = new GiraffeDataSource((Converter<String,
                List<SystemRule>>) source -> {
            try {
                return LyJacksonUtil.fromJson(source, new TypeReference<List<SystemRule>>() {
                });
            } catch (Exception e) {
                return Lists.newArrayList();
            }
        }, "systems");
        return giraffeDataSource;
    }
}
