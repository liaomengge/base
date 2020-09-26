package cn.ly.base_common.dayu.guava.reload;

import cn.ly.base_common.dayu.guava.consts.GuavaRateLimitConst;
import cn.ly.base_common.dayu.guava.domain.FlowRule;
import cn.ly.base_common.dayu.guava.interceptor.GuavaRateLimitHandlerInterceptor;
import cn.ly.base_common.utils.json.LyJacksonUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.util.concurrent.RateLimiter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Created by liaomengge on 2019/8/12.
 */
public class GuavaRateLimitReloadListener implements EnvironmentAware, ApplicationListener<EnvironmentChangeEvent> {

    private static final Logger log = LyLogger.getInstance(GuavaRateLimitReloadListener.class);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Optional<String> rateLimitChange = event.getKeys().stream().filter(val -> StringUtils.startsWithIgnoreCase(val,
                buildRuleKey())).findFirst();
        rateLimitChange.ifPresent(val -> loadAndUpdateRules());
    }

    private void loadAndUpdateRules() {
        String flowRules = environment.getProperty(buildRuleKey());
        if (StringUtils.isNotBlank(flowRules)) {
            List<FlowRule> flowRuleList = null;
            try {
                flowRuleList = LyJacksonUtil.fromJson(flowRules, new TypeReference<List<FlowRule>>() {
                });
            } catch (Exception e) {
                log.warn("[Guava RateLimit], [flow] rule parse exception", e);
            }
            if (CollectionUtils.isNotEmpty(flowRuleList)) {
                flowRuleList = flowRuleList.stream()
                        .filter(flowRule -> StringUtils.isNotBlank(flowRule.getResource()) && flowRule.getCount() > 0.0d)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(flowRuleList)) {
                    ConcurrentMap<String, RateLimiter> rateLimiterMap =
                            GuavaRateLimitHandlerInterceptor.getResourceLimiterMap();
                    rateLimiterMap.clear();
                    flowRuleList.stream().forEach(flowRule -> rateLimiterMap.put(flowRule.getResource(),
                            RateLimiter.create(flowRule.getCount())));
                    log.info("[Guava RateLimit], [flow] rule config be updated to: " + flowRuleList.toString());
                }
            }
        }
    }

    private String buildRuleKey() {
        return GuavaRateLimitConst.GUAVA_RATE_LIMIT_PREFIX + ".rule.flows";
    }
}