package cn.ly.base_common.dayu.guava;

import cn.ly.base_common.dayu.guava.consts.GuavaRateLimitConst;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import lombok.Data;

/**
 * Created by liaomengge on 2019/8/12.
 */
@Data
@ConfigurationProperties(prefix = GuavaRateLimitConst.GUAVA_RATE_LIMIT_PREFIX)
public class GuavaRateLimitProperties {

    private boolean enabled;
    private RuleProperties rule = new RuleProperties();
    private InterceptorProperties interceptor = new InterceptorProperties();

    @Data
    public static class RuleProperties {
        private String flows;
    }

    @Data
    public static class InterceptorProperties {
        private List<String> urlPatterns;
        private int order = Ordered.HIGHEST_PRECEDENCE;
    }
}
