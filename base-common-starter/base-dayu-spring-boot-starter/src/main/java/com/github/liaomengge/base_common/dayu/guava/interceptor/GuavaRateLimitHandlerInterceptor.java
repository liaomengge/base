package com.github.liaomengge.base_common.dayu.guava.interceptor;

import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.liaomengge.base_common.dayu.consts.DayuConst;
import com.github.liaomengge.base_common.dayu.guava.GuavaRateLimitProperties;
import com.github.liaomengge.base_common.dayu.guava.callback.WebCallbackManager;
import com.github.liaomengge.base_common.dayu.guava.domain.FlowRule;
import com.github.liaomengge.base_common.support.meter._MeterRegistrys;
import com.github.liaomengge.base_common.utils.collection.LyMapUtil;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.google.common.util.concurrent.RateLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by liaomengge on 2019/8/12.
 */
@Slf4j
@AllArgsConstructor
public class GuavaRateLimitHandlerInterceptor extends HandlerInterceptorAdapter {
    
    @Getter
    private static final ConcurrentMap<String, RateLimiter> resourceLimiterMap = Maps.newConcurrentMap();

    private MeterRegistry meterRegistry;
    private GuavaRateLimitProperties guavaRateLimitProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String uriTarget = StringUtils.defaultString(pattern, FilterUtil.filterTarget(request));
        List<FlowRule> flowRules = null;
        try {
            flowRules = LyJacksonUtil.fromJson(guavaRateLimitProperties.getRule().getFlows(),
                    new TypeReference<List<FlowRule>>() {
                    });
        } catch (Exception e) {
            log.warn("[Guava RateLimit], [flow] rule parse exception", e);
        }
        if (CollectionUtils.isNotEmpty(flowRules)) {
            Optional<FlowRule> flowRuleOptional =
                    flowRules.stream().filter(val -> StringUtils.equalsIgnoreCase(uriTarget, val.getResource())).findFirst();
            if (flowRuleOptional.isPresent() && flowRuleOptional.get().getCount() > 0.0d) {
                log.info("[Guava RateLimit Interceptor], Uri Path: {}", uriTarget);
                RateLimiter rateLimiter = LyMapUtil.computeIfAbsent(resourceLimiterMap, uriTarget,
                        key -> RateLimiter.create(flowRuleOptional.get().getCount()));
                if (!rateLimiter.tryAcquire()) {
                    WebCallbackManager.getUrlRateLimitHandler().blocked(request, response);
                    String finalUriTarget = uriTarget;
                    _MeterRegistrys.counter(meterRegistry, DayuConst.METRIC_GUAVA_LIMITED_PREFIX + finalUriTarget).ifPresent(Counter::increment);
                    return false;
                }
            }
        }
        return true;
    }
}
