package cn.ly.base_common.dayu.guava.interceptor;

import cn.ly.base_common.dayu.consts.DayuConst;
import cn.ly.base_common.dayu.guava.GuavaRateLimitProperties;
import cn.ly.base_common.dayu.guava.callback.WebCallbackManager;
import cn.ly.base_common.dayu.guava.domain.FlowRule;
import cn.ly.base_common.utils.json.MwJsonUtil;
import cn.ly.base_common.utils.log4j2.MwLogger;
import com.alibaba.csp.sentinel.adapter.servlet.util.FilterUtil;
import com.alibaba.fastjson.TypeReference;
import com.google.common.util.concurrent.RateLimiter;
import com.timgroup.statsd.StatsDClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.Logger;
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
@AllArgsConstructor
public class GuavaRateLimitHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = MwLogger.getInstance(GuavaRateLimitHandlerInterceptor.class);

    @Getter
    private static final ConcurrentMap<String, RateLimiter> resourceLimiterMap = Maps.newConcurrentMap();

    private StatsDClient statsDClient;
    private GuavaRateLimitProperties guavaRateLimitProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String uriTarget = StringUtils.defaultString(pattern, FilterUtil.filterTarget(request));
        List<FlowRule> flowRules = null;
        try {
            flowRules = MwJsonUtil.fromJson(guavaRateLimitProperties.getRule().getFlows(),
                    new TypeReference<List<FlowRule>>() {
                    });
        } catch (Exception e) {
            logger.warn("[Guava RateLimit], [flow] rule parse exception", e);
        }
        if (CollectionUtils.isNotEmpty(flowRules)) {
            Optional<FlowRule> flowRuleOptional =
                    flowRules.stream().filter(val -> StringUtils.equalsIgnoreCase(uriTarget, val.getResource())).findFirst();
            if (flowRuleOptional.isPresent() && flowRuleOptional.get().getCount() > 0.0d) {
                logger.info("[Guava RateLimit Interceptor], Uri Path: {}", uriTarget);
                RateLimiter rateLimiter = resourceLimiterMap.computeIfAbsent(uriTarget,
                        key -> RateLimiter.create(flowRuleOptional.get().getCount()));
                if (!rateLimiter.tryAcquire()) {
                    WebCallbackManager.getUrlRateLimitHandler().blocked(request, response);
                    String finalUriTarget = uriTarget;
                    Optional.ofNullable(statsDClient).ifPresent(val -> statsDClient.increment(DayuConst.METRIC_GUAVA_LIMITED_PREFIX + finalUriTarget));
                    return false;
                }
            }
        }
        return true;
    }
}
