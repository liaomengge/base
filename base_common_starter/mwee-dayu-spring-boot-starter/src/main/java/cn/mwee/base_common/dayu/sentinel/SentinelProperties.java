package cn.mwee.base_common.dayu.sentinel;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.util.List;

import static cn.mwee.base_common.dayu.sentinel.consts.SentinelConst.SENTINEL_PREFIX;

/**
 * Created by liaomengge on 2019/8/9.
 */
@Data
@ConfigurationProperties(prefix = SENTINEL_PREFIX)
public class SentinelProperties {

    private boolean enabled;
    private RuleProperties rule = new RuleProperties();
    private FilterProperties filter = new FilterProperties();
    private Servlet servlet = new Servlet();

    @Data
    public static class RuleProperties {
        private String flows;
        private String degrades;
        private String authorities;
        private String paramFlows;
        private String systems;
    }

    @Data
    public static class FilterProperties {
        private List<String> urlPatterns;
        private String excludedUris;//逗号分隔
        private int order = Ordered.HIGHEST_PRECEDENCE;
    }

    @Data
    public static class Servlet {

        /**
         * The process page when the flow control is triggered.
         */
        private String blockPage;
    }
}
